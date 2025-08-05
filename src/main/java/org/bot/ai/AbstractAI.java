package org.bot.ai;

import org.apache.commons.lang3.StringUtils;
import org.bot.ResponseAI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class AbstractAI {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAI.class);

    public abstract String getName();

    public abstract String getApiKey();

    public abstract String getModel();

    public abstract String getRequestModel();

    public abstract String getUri();

    public ResponseAI getResponse(String question) {
        String requestBody = getRequestModel().formatted(getModel(), question);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getUri()))
                .timeout(Duration.ofSeconds(80))  // таймаут ожидания ответа
                .header("Authorization", "Bearer " + getApiKey())
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "https://your-site.com") // Опционально (OpenRouter просит указать источник)
                .header("X-Title", "My App") // Опционально
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.error("send error: ", e);
            return new ResponseAI(StringUtils.EMPTY, 501);
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            String content = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return new ResponseAI(content, response.statusCode());
        } else {
            logger.error("code error: " + response.statusCode());
        }
        return new ResponseAI(StringUtils.EMPTY, response.statusCode());
    }
}
