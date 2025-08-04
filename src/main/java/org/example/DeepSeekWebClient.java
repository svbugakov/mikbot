package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.time.Duration;

public class DeepSeekWebClient {

    private static final Logger logger = LoggerFactory.getLogger(DeepSeekWebClient.class);

    public String request(String question) throws Exception {
        String apiKey = "xxx"; // Замените на свой ключ
        String model = "deepseek/deepseek-chat-v3-0324:free"; // Или другой вариант модели DeepSeek

        String requestBody = """
            {
                "model": "%s",
                "messages": [
                    {"role": "user", "content": "%s"}
                ]
            }
            """.formatted(model, question);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://openrouter.ai/api/v1/chat/completions"))
                .timeout(Duration.ofSeconds(80))  // таймаут ожидания ответа
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .header("HTTP-Referer", "https://your-site.com") // Опционально (OpenRouter просит указать источник)
                .header("X-Title", "My App") // Опционально
                .POST(BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JSONObject jsonResponse = new JSONObject(response.body());
            JSONArray choices = jsonResponse.getJSONArray("choices");
            String content = choices.getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");

            return content;
        } else {
            logger.error("code error: " + response.statusCode());
        }
        return "";
    }
}