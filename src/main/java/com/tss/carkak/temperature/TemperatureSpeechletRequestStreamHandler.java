package com.tss.carkak.temperature;

import java.util.HashSet;
import java.util.Set;

import com.amazon.speech.speechlet.lambda.SpeechletRequestStreamHandler;

/**
 * This class could be the handler for an AWS Lambda function powering an Alexa Skills Kit
 * experience. To do this, simply set the handler field in the AWS Lambda console to
 * "com.tss.carkak.temperature.TemperatureSpeechletRequestStreamHandler" For this to work, you'll also need to build
 * this project using the Gradle task and upload the resulting zip file to power
 * your function.
 */
public final class TemperatureSpeechletRequestStreamHandler extends SpeechletRequestStreamHandler {
    private static final Set<String> supportedApplicationIds = new HashSet<String>();
    static {
        /*
         * This Id can be found on https://developer.amazon.com/edw/home.html#/ "Edit" the relevant
         * Alexa Skill and put the relevant Application Ids in this Set.
         */
//        supportedApplicationIds.add("amzn1.ask.skill.66cc6e2f-03cd-48d4-a07c-c97996b49183");
    }

    public TemperatureSpeechletRequestStreamHandler() {
        super(new TemperatureSpeechlet(), supportedApplicationIds);
    }
}
