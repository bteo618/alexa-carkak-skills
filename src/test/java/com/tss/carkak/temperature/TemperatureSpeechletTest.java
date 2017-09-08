package com.tss.carkak.temperature;

import static com.tss.carkak.temperature.TemperatureSpeechlet.LIGHT_TURN_ON_END_POINT;
import static com.tss.carkak.temperature.TemperatureSpeechlet.LIGHT_TURN_OFF_END_POINT;
import static org.junit.Assert.*;
import static com.tss.carkak.temperature.TemperatureSpeechlet.ITEM_PK_USER_ID;
import static com.tss.carkak.temperature.TemperatureSpeechlet.ITEM_USER_TEMPERATURE;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.tss.carkak.temperature.storage.UserProfile;

import org.apache.http.client.HttpClient;
import org.apache.http.HttpResponse;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TemperatureSpeechletTest {

  private TemperatureSpeechlet temperatureSpeechlet;

  private UserProfile user;

  private SpeechletResponse speechletResponse;
  private Intent intent;
  private Session session;
  private String temperature = "";

  @Before
  public void setUp() {
    temperature = "25";
    intent = newIntent("DEFAULT");
    session = Session.builder().withSessionId("DEFAULT_SESSION_ID").build();

    temperatureSpeechlet = Mockito.spy(new TemperatureSpeechlet());
    doNothing().when(temperatureSpeechlet).setUpNewHttpClient();
    doNothing().when(temperatureSpeechlet).clearResponse(Mockito.any(HttpResponse.class));
    temperatureSpeechlet.table = mockTable();
    temperatureSpeechlet.client = mock(HttpClient.class);


    user = new UserProfile("DEFAULT",temperature);
    temperatureSpeechlet.user = user;
  }

  private Table mockTable() {
    Table table = mock(Table.class);
    return table;
  }

  private Intent newIntent(String intentName) {
    return Intent.builder().withName(intentName).build();
  }

  @Test
  public void increaseTemperatureResponseTest() {
    speechletResponse = temperatureSpeechlet.increaseTemperatureResponse();
    verify(temperatureSpeechlet, times(1)).newAskResponse(Mockito.any(SimpleCard.class), Mockito.any(PlainTextOutputSpeech.class), Mockito.any(Reprompt.class));
  }

  @Test
  public void decreaseTemperatureResponseTest() {
    speechletResponse = temperatureSpeechlet.decreaseTemperatureResponse(session);
    verify(temperatureSpeechlet, times(1)).newAskResponse(Mockito.any(SimpleCard.class), Mockito.any(PlainTextOutputSpeech.class), Mockito.any(Reprompt.class));
  }

  @Test
  public void setTemperatureResponseTest() {
    ArgumentCaptor<String> captor =  ArgumentCaptor.forClass(String.class);
    speechletResponse = temperatureSpeechlet.setTemperatureResponse(intent, temperature, session);
    verify(temperatureSpeechlet.table, times(1)).putItem(Mockito.any(Item.class));
    assertEquals(user.getUserId(), temperatureSpeechlet.item.get(ITEM_PK_USER_ID).toString());
    assertEquals(temperature, temperatureSpeechlet.item.get(ITEM_USER_TEMPERATURE).toString());
    verify(temperatureSpeechlet, times(1)).getAskSpeechletResponse(captor.capture());
    assertTrue(captor.getValue().indexOf(temperature) > -1);
  }

  @Test
  public void httpPostTurnOnLightTest() throws IOException {
    temperatureSpeechlet.httpPostTurnOnLight();
    assertEquals(temperatureSpeechlet.httpPost.getURI().toString(), LIGHT_TURN_ON_END_POINT);
  }

  @Test
  public void httpPostTurnOffLightTest() throws IOException {
    temperatureSpeechlet.httpPostTurnOffLight();
    assertEquals(temperatureSpeechlet.httpPost.getURI().toString(), LIGHT_TURN_OFF_END_POINT);
  }
}