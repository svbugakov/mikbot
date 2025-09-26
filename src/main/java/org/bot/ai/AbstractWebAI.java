package org.bot.ai;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public abstract class AbstractWebAI extends AbstractAICommon {
    @Override
    public ResponseAI getResponse(Question question) {
        final ResponseAI responseAIR = tryNeedRedirect(question);
        if (responseAIR.getRedirectAiName() != null) {
            logger.info("redirect question:{}", question.getMessage());
            return responseAIR;
        }

        String requestBody = getRequestModel().formatted(getModel(), question.getMessage());

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
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            JSONObject jsonResponse = new JSONObject(response.body());
            String content;
            try {
                JSONArray choices = jsonResponse.getJSONArray("choices");
                content = choices.getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
            } catch (JSONException e) {
                logger.error("error in get choices", e);
                return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
            }
            return new ResponseAI(content, StatusResponse.SUCCESS, QuestionGoal.TEXT);
        } else {
            logger.error("code error: " + response.statusCode());
        }
        return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
    }
}
