package io.intellecttitans.springbootbackend.configurations;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import org.json.JSONArray;
import org.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.intellecttitans.springbootbackend.configurations.ChatBot;


@Component
public class ChatBot {
    private static String endpoint;

    private static String key;

    private static String prompt;

    @Value("${chatBot.endpoint}")
    private void setEndpoint(String endpoint) {
        ChatBot.endpoint = endpoint;
    }

    @Value("${chatBot.key}")
    private void setKey(String key) {
        ChatBot.key = key;
    }

    @Value("Imagine you are an world champion puzzle solver. " +
            "I want you to solve the following puzzle from the examples provided:\n" +
            "Given a long URL, shorten it while retaining a meaningful set of characters." +
            "The new short URL must be of length 5. It should under no circumstance exceed it." +
            "If you found such URLs, answer in a JSON format: {'shorturl': ['url1', 'url2'...,'url5']}. " +
            "Find all the possible combinations for the shorturl and order them in most favourable to least favourable\n. " +
            "If you do not find such short urls, respond with an empty string JSON.\n\n" +
            "Example:\n Long URL = https://www.baeldung.com/spring-postconstruct-predestroy\n" +
            "{'shorturl': ['baepc', 'bsbpc', 'spcst']}\n" +
            "LongURL = ")
    public void setPrompt(String prompt) {
        ChatBot.prompt = prompt;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBot.class);

    public JSONObject sendQuery(String input) {
        // Build input and API key params
        JSONObject payload = new JSONObject();
        JSONObject message = new JSONObject();
        JSONArray messageList = new JSONArray();

        message.put("role", "user");
        message.put("content", prompt + input);
        messageList.put(message);

        payload.put("model", "gpt-3.5-turbo"); // model is important
        // payload.put("response_format", "{'type': 'json_object'}");   // Doesnt work yet, but should be soon
        payload.put("messages", messageList);
        payload.put("temperature", 0.7);

        StringEntity inputEntity = new StringEntity(payload.toString(), ContentType.APPLICATION_JSON);

        // Build POST request
        try(
                CloseableHttpClient httpClient = HttpClients.createDefault();) {
            System.out.println(endpoint);

            HttpPost post = new HttpPost(endpoint);

            post.setEntity(inputEntity);
            post.setHeader("Authorization", "Bearer " + key);
            post.setHeader("Content-Type", "application/json");
//                 Send POST request and parse response

            System.out.println("Chatbot client running");
             HttpResponse response = httpClient.execute(post);
            HttpEntity resEntity = response.getEntity();
            System.out.println("Request response = " + response);

            String result = new String(resEntity.getContent().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("Request result = " + result);
            JSONObject resJson = new JSONObject(result);

            if (resJson.has("error")) {
                String errorMsg = resJson.getString("error");
                LOGGER.error("Chatbot API error: {}", errorMsg);
                return new JSONObject(String.format("{Error: %s}", errorMsg));
            }

            JSONArray responseArray = resJson.getJSONArray("choices");
            // List<String> responseList = new ArrayList<>();

            // for (int i = 0; i < responseArray.length(); i++) {
            //     JSONObject responseObj = responseArray.getJSONObject(i);
            //     String responseString = responseObj.getJSONObject("message").getString("content");
            //     responseList.add((new JSONObject(responseString)).toString());
            // }
            JSONObject responseObj = responseArray.getJSONObject(0);
            JSONObject contentJsonObject = new JSONObject(responseObj.getJSONObject("message").getString("content"));
            System.out.println(contentJsonObject);
//
            // // Convert response list to JSON and return it
            // Gson gson = new Gson();
            // String jsonResponse = gson.toJson(responseList);
            // System.out.println(jsonResponse);
            return contentJsonObject;
        } catch (Exception e) {
            LOGGER.error("Error sending request: {}", e.getMessage());
            return new JSONObject(String.format("{Error: %s}", e.getMessage()));
        }
    }
}