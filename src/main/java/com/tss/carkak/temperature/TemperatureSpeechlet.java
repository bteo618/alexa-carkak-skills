package com.tss.carkak.temperature;

import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.Speechlet;
import com.amazon.speech.speechlet.SpeechletException;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.tss.carkak.temperature.storage.UserProfile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This Carkak skill shows how to adjust the temperature in car through speechlet requests.
 */
public class TemperatureSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(TemperatureSpeechlet.class);
  private static final String SLOT_TEMPERATURE = "SetTemperature";
  private Table table;
  private UserProfile user;
  private final String DEFAULT_USER = "bj";

    @Override
    public void onSessionStarted(final SessionStartedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionStarted requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(final LaunchRequest request, final Session session)
            throws SpeechletException {
        log.info("onLaunch requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        DynamoDB dynamoDB = new DynamoDB(getDynamoDBClient());
        table = dynamoDB.getTable("UserProfile");
        Item item = table.getItem("UserId",DEFAULT_USER);
        if(null!=item){
          log.info(item.toString());
          user = new UserProfile(item.get("UserId").toString(),item.get("UserTemp").toString());
        }else{
          log.info("empty");
        }

        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;
      StringBuilder stringBuilder2 = new StringBuilder();

      if ("IntroduceIntent".equals(intentName)) {
        return setIntroduceResponse(intent, session);
      }
      else if ("IncreaseTemperatureIntent".equals(intentName)) {
            return increaseTemperatureResponse();
        }
        else if ("DecreaseTemperatureIntent".equals(intentName)) {
            return decreaseTemperatureResponse();
        }
        else if ("SetTemperatureIntent".equals(intentName)) {
          try {
            stringBuilder2.append(requestHttp(intent));
          } catch (IOException e) {
            e.printStackTrace();
          }
          return setTemperatureResponse(intent, stringBuilder2.toString(), session);
        }
        else if ("YesIntent".equals(intentName)) {
          return yesResponse();
        }
        else if ("AMAZON.HelpIntent".equals(intentName)) {
            return getHelpResponse();
        }
        else if ("AMAZON.StopIntent".equals(intentName)) {
          PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
          outputSpeech.setText("enjoy your drive bj, good bye.");

          return SpeechletResponse.newTellResponse(outputSpeech);
        }
        else if ("AMAZON.CancelIntent".equals(intentName)) {
          PlainTextOutputSpeech outputSpeech = new PlainTextOutputSpeech();
          outputSpeech.setText("enjoy your drive bj, good bye.");

          return SpeechletResponse.newTellResponse(outputSpeech);
        }
        else {
            throw new SpeechletException("Invalid Intent");
        }
    }

  private SpeechletResponse setIntroduceResponse(Intent intent, Session session) {

    UserProfile user = new UserProfile();
    user.setUserId(intent.getSlot("UserId").getValue());

    Item item = new Item();
    item.withString("UserId",intent.getSlot("UserId").getValue())
    .withString("UserTemp","26");

    table.putItem(item);

    String speechText = "the user profile is saved.";

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Carkak Temperature");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    // Create reprompt
    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);

    return SpeechletResponse.newTellResponse(speech, card);
  }

    @Override
    public void onSessionEnded(final SessionEndedRequest request, final Session session)
            throws SpeechletException {
        log.info("onSessionEnded requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());
        // any cleanup logic goes here
    }

    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getWelcomeResponse() {
        String currentTemp = requestCurrentTemp();
        String speechText = "Hi %s, the current temperature is set to %s degrees.";
        speechText = String.format(speechText, user.getUserId(), user.getUserTemp());

        String repromptString = "Hello, are you still there?";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Carkak Home Screen");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        PlainTextOutputSpeech repromptText = new PlainTextOutputSpeech();
        repromptText.setText(repromptString);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(repromptText);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

  private String requestCurrentTemp() {
      String currentTemp = "25";
    return currentTemp;
  }

  /**
     * Creates a {@code SpeechletResponse} for the temperature intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse increaseTemperatureResponse() {
        String speechText = "what temperature would you like me to set?";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Carkak Temperature");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

  private SpeechletResponse decreaseTemperatureResponse() {
    String speechText = "u want to decrease the temperature?";

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Carkak Temperature");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);

    return SpeechletResponse.newAskResponse(speech, reprompt, card);
  }

  private String requestHttp(Intent intent) throws IOException {
    HttpClient client = new DefaultHttpClient();
    List<NameValuePair> params = new ArrayList<NameValuePair>();
    params.add(new BasicNameValuePair("temp", intent.getSlot(SLOT_TEMPERATURE).getValue()));
    Gson gson = new Gson();

//    for (NameValuePair nvp : params) {
//      String name = nvp.getName();
//      String value = nvp.getValue();
//      System.out.println("basic name: " + name + " -- " + value);
//    }

    HttpPost httpPost = new HttpPost("http://ec2-13-229-56-107.ap-southeast-1.compute.amazonaws.com/methods/setTemp");
    httpPost.setEntity(new UrlEncodedFormEntity(params));
    HttpResponse resp = client.execute(httpPost);
    EntityUtils.consumeQuietly(resp.getEntity());

    HttpGet request = new HttpGet("http://ec2-13-229-56-107.ap-southeast-1.compute.amazonaws.com/methods/getTemp");
    HttpResponse response = client.execute(request);

    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
    String line = "";
    StringBuilder stringBuilder = new StringBuilder();
    while ((line = rd.readLine()) != null) {

      stringBuilder.append(line);
    }
    TemperatureBean temperatureBean = gson.fromJson(stringBuilder.toString(), TemperatureBean.class);
    String message = org.apache.commons.io.IOUtils.toString(rd);
    System.out.println("PLSSSS: " + temperatureBean.getTemp());
return temperatureBean.getTemp();
  }

  private SpeechletResponse setTemperatureResponse(Intent intent, String temperature, Session session) {
    String speechText = "the temperature is now set to %s degree, is that ok?";
    speechText = String.format(speechText, temperature);

    Item item = new Item();
    item.withString("UserId",user.getUserId())
        .withString("UserTemp",temperature);

    table.putItem(item);

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Carkak Temperature");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    Reprompt reprompt = new Reprompt();
    reprompt.setOutputSpeech(speech);
//
//    String speech2 = "the temperature is now set at " + temperature + " degrees, cool or not?";


    return getTellSpeechletResponse(speechText);
  }

  private SpeechletResponse getTellSpeechletResponse(String speechText) {
    SimpleCard card = new SimpleCard();
    card.setTitle("Session");
    card.setContent(speechText);

    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    return SpeechletResponse.newTellResponse(speech, card);
  }

  private SpeechletResponse yesResponse() {
    String speechText = "enjoy your drive bj, good bye.";

    // Create the Simple card content.
    SimpleCard card = new SimpleCard();
    card.setTitle("Carkak Temperature");
    card.setContent(speechText);

    // Create the plain text output.
    PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
    speech.setText(speechText);

    return SpeechletResponse.newTellResponse(speech, card);
  }

    /**
     * Creates a {@code SpeechletResponse} for the help intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse getHelpResponse() {
        String speechText = "hi bj, how can i help you?";

        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Carkak Temperature");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        // Create reprompt
        Reprompt reprompt = new Reprompt();
        reprompt.setOutputSpeech(speech);

        return SpeechletResponse.newAskResponse(speech, reprompt, card);
    }

  private AmazonDynamoDB getDynamoDBClient () {
    AmazonDynamoDBClientBuilder builder = AmazonDynamoDBClientBuilder.standard();

    return builder.build();
  }
}
