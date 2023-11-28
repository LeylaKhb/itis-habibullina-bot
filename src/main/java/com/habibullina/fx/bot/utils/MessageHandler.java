package com.habibullina.fx.bot.utils;

import com.habibullina.fx.bot.client.HttpClientImpl;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.*;


public class MessageHandler {

    private boolean citySearch = false;
    private boolean isStarted = false;
    private boolean currencySearch = false;
    private static final String CHAT_STARTING_PHRASE = "You are going to chat";
    private static final String HELP_ANSWER = "You can get weather by \"/weather\", can get  currency course" +
            " by \"/currency\" and go to chat by \"/chat\" and end exit by \"/end\"";
    private static final String NOT_STARTED_ANSWER = "Please start bot by \"/start\"";
    private static final String START_ANSWER = "You started chat. ";
    private static final String START_COMMAND = "/start";
    private static final String EXIT_COMMAND = "/exit";
    private static final String WEATHER_COMMAND = "/weather";
    private static final String CURRENCY_COMMAND = "/currency";
    private static final String HELP_COMMAND = "/help";
    private static final String CHAT_COMMAND = "/chat";
    private static final String CURRENCY_ANSWER = "Enter currency please by XXX format";
    private static final String WEATHER_ANSWER = "Enter city please";
    private static final String EXIT_ANSWER = "Bye bye";





    public String handleMessage(String message) {
        String answer = HELP_ANSWER;
        if (!isStarted) {
            if (!message.equals(START_COMMAND)) {
                return NOT_STARTED_ANSWER;
            }
            isStarted = true;
            return START_ANSWER + HELP_ANSWER;
        }
        if (citySearch) {
            citySearch = false;
            return getWeather(message);
        }
        if (currencySearch) {
            currencySearch = false;
            return getCourse(message);
        }
        switch (message) {
            case EXIT_COMMAND -> {
                answer = EXIT_ANSWER;
                isStarted = false;
            }
            case WEATHER_COMMAND -> {
                answer = WEATHER_ANSWER;
                citySearch = true;
            }
            case CURRENCY_COMMAND -> {
                answer = CURRENCY_ANSWER;
                currencySearch = true;
            }
            case HELP_COMMAND -> answer = HELP_ANSWER;
            case CHAT_COMMAND -> answer = CHAT_STARTING_PHRASE;
        }
        return answer;
    }

    private String getCourse(String currency) {
        Map<String, String> params = new HashMap<>();

        HttpClientImpl httpClient = new HttpClientImpl();
        String currencyAnswer = httpClient.get("https://www.cbr-xml-daily.ru/daily_json.js",
                params);

        String answer;
        JsonElement currencyJSON = new JsonParser().parse(currencyAnswer);
        JsonObject valuteJSON = currencyJSON.getAsJsonObject().get("Valute").getAsJsonObject();
        if (valuteJSON.getAsJsonObject().get(currency) != null) {
            JsonObject answerJSON = valuteJSON.getAsJsonObject().get(currency).getAsJsonObject();
            String currAnswer = answerJSON.get("Value").getAsString();

            answer = currency + ": " + currAnswer;
        } else {
            answer = "Can't get currency course";
        }
        return answer;
    }

    public String getChatStartingPhrase() {
        return CHAT_STARTING_PHRASE;
    }

    public String getNotStartedAnswer() {
        return NOT_STARTED_ANSWER;
    }

    private String getWeather(String cityName) {
        String APIKey = "7158fed5a7566d75dcb1c18bd78573eb";
        Map<String, String> params = new HashMap<>();
        params.put("q", cityName);
        params.put("appid", APIKey);

        HttpClientImpl httpClient = new HttpClientImpl();
        String forecast = httpClient.get("https://api.openweathermap.org/data/2.5/weather",
                params);

        String answer;

        if (!forecast.equals("")) {
            JsonElement forecastJSON = new JsonParser().parse(forecast);
            JsonObject mainInForecast = forecastJSON.getAsJsonObject().get("main").getAsJsonObject();
            JsonObject weatherInForecast = forecastJSON.getAsJsonObject().get("weather").getAsJsonArray()
                    .get(0).getAsJsonObject();

            String temp = String.valueOf(mainInForecast.get("temp").getAsFloat() - 273);
            String humidity = mainInForecast.get("humidity").getAsString();
            String precipitation = weatherInForecast.get("description").getAsString();

            String answerBuilder = "temp: " + temp + ", humidity: " + humidity +
                    ", precipitation: " + precipitation;
            answer = String.valueOf(answerBuilder);
        } else {
            answer = "Can't get weather";
        }
        return answer;
    }
}
