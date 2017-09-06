package com.tss.carkak.temperature;

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

/**
 * This Carkak skill shows how to adjust the temperature in car through speechlet requests.
 */
public class TemperatureSpeechlet implements Speechlet {
    private static final Logger log = LoggerFactory.getLogger(TemperatureSpeechlet.class);

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
        return getWelcomeResponse();
    }

    @Override
    public SpeechletResponse onIntent(final IntentRequest request, final Session session)
            throws SpeechletException {
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(),
                session.getSessionId());

        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        if ("IncreaseTemperatureIntent".equals(intentName)) {
            return increaseTemperatureResponse();
        }
        else if ("DecreaseTemperatureIntent".equals(intentName)) {
            return decreaseTemperatureResponse();
        }
        else if ("SetTemperatureIntent".equals(intentName)) {
            return setTemperatureResponse();
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

    /**
     * Creates a {@code SpeechletResponse} for the temperature intent.
     *
     * @return SpeechletResponse spoken and visual response for the given intent
     */
    private SpeechletResponse increaseTemperatureResponse() {
        String speechText = "u want to increase the temperature?";

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

  private SpeechletResponse setTemperatureResponse() {
    String speechText = "the temperature is now set to 25 degree, is that ok?";

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
}
