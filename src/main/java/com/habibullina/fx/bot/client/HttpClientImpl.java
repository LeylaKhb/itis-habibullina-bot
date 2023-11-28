package com.habibullina.fx.bot.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpClientImpl implements HttpClient {
    @Override
    public String get(String url, Map<String, String> params) {
        String method = "GET";
        return getResponseFromURL(url, params, method);

    }

    @Override
    public String post(String url, Map<String, String> params) {
        String method = "POST";
        return getResponseFromURL(url, params, method);
    }

    @Override
    public String put(String url, Map<String, String> params) {
        String method = "PUT";
        return getResponseFromURL(url, params, method);
    }

    @Override
    public String delete(String url, Map<String, String> params) {
        String method = "DELETE";
        return getResponseFromURL(url, params, method);
    }

    private String getResponseFromURL(String url, Map<String, String> params, String method) {
        try {
            URL fullUrl = new URL(url);
            switch (method) {
                case "GET" -> fullUrl = new URL(buildUrl(url, params));
                case "POST" -> fullUrl = new URL(url);
                case "DELETE", "PUT" -> {
                    String id = getId(url, params);
                    fullUrl = new URL(url + "/" + id);
                }
            }

            HttpURLConnection connection = (HttpURLConnection) fullUrl.openConnection();

            connection.setRequestMethod(method);
            setHeadersAndTimeout(connection, method);

            if (method.equals("POST")) {
                connection.setDoOutput(true);
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonInput = objectMapper.writeValueAsString(params);

                try (OutputStream outputStream = connection.getOutputStream()) {
                    byte[] input = jsonInput.getBytes(StandardCharsets.UTF_8);
                    outputStream.write(input, 0, input.length);
                }
            }

            String response = readResponse(connection);
            connection.disconnect();
            return response;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String getId(String url, Map<String, String> params) {
        String jsonStr = get(url, params);
        StringBuilder id = new StringBuilder();
        for (int i = 7; i < jsonStr.length(); i++) {
            char jsonChar = jsonStr.charAt(i);
            if (('0' <= jsonChar) && (jsonChar <= '9')) {
                id.append(jsonChar);
            } else {
                break;
            }
        }
        return id.toString();
    }

    private String buildUrl(String url, Map<String, String> params) {
        StringBuilder urlBuilder = new StringBuilder(url);
        if (!params.isEmpty()) {
            urlBuilder.append("?");
            for (Map.Entry<String, String> pair : params.entrySet()) {
                String key = pair.getKey().replaceAll(" ", "%20");
                String value = pair.getValue().replaceAll(" ", "%20");
                urlBuilder.append(key).append("=").append(value).append("&");
            }
            urlBuilder.deleteCharAt(urlBuilder.length() - 1);
        }
        url = urlBuilder.toString();
        return url;
    }

    private void setHeadersAndTimeout(HttpURLConnection connection, String method) {
        connection.setRequestProperty("Content-Type", "application/json");
        if (!method.equals("GET")) {
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Authorization", "Bearer e21d7666f3aa8df7819218b3d87bdf57b3e2f96c7077ae1b5c99c76fe82ec069");
        }
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);

    }

    private static String readResponse(HttpURLConnection connection) throws IOException {
        if (connection != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder content = new StringBuilder();
                String input;
                while ((input = reader.readLine()) != null) {
                    content.append(input);
                }
                return content.toString();
            }
            catch (IOException e) {
                return "";
            }
        }
        return null;
    }

}
