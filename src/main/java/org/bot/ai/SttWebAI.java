package org.bot.ai;

import chat.giga.http.client.HttpClientException;
import org.apache.commons.lang3.StringUtils;
import org.bot.SslContextKeeper;
import org.bot.ai.entity.Question;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * class to translate speech to text - stt
 */
public class SttWebAI extends AbstractAICommon<String> {
    private static final Logger logger = LoggerFactory.getLogger(SttWebAI.class);
    private SslContextKeeper sslContextKeeper;
    private final String url;

    public SttWebAI(String url, SslContextKeeper sslContextKeeper) {
        this.url = url;
        this.sslContextKeeper = sslContextKeeper;
    }


    @Override
    public String getName() {
        return "SttWebAI";
    }

    @Override
    public String getApiKey() {
        return "";
    }

    @Override
    public String getModel() {
        return "SttWebAI";
    }

    @Override
    public String getRequestModel() {
        return "SttWebAI";
    }

    @Override
    public String getUri() {
        return url;
    }

    @Override
    public ResponseAI getResponse(Question question) {
        try {
            return convert(question);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseAI convert(final Question question) {
        String task = "transcribe";
        String language = "ru";

        // Создание multipart boundary
        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();

        byte[] multipartBody;
        try {
            multipartBody = createMultipartBody(question.getBytes(), boundary);
        } catch (Exception e) {
            logger.error("send error: ", e);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "?task=" + task + "&language=" + language))
                .timeout(Duration.ofSeconds(80))
                .header("Content-Type", "multipart/form-data; boundary=" + boundary)
                // Дополнительные заголовки, если нужны
                .header("User-Agent", "My Java Client")
                .POST(HttpRequest.BodyPublishers.ofByteArray(multipartBody))
                .build();


        // Создание HTTP клиента и отправка запроса
        HttpClient client = HttpClient.newBuilder()
                .sslContext(sslContextKeeper.getSslContext("stt")) // ваш SSLContext если нужно
                .build();

        HttpResponse<String> response = null;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            logger.error("send error: ", e);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        } catch (HttpClientException ex) {
            logger.error("HTTP error occurred: ", ex);
            logger.error(ex.bodyAsString());
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return new ResponseAI(response.body(), StatusResponse.SUCCESS, QuestionGoal.AUDIO);
        } else {
            logger.error("send error: {}, {}", response.body(), response.statusCode());
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }
    }



    private byte[] createMultipartBody(byte[] audioData, String boundary) throws Exception {
        // Чтение аудио файла

        String fileName = "mikfile.ogg";

        // Строим multipart тело
        StringBuilder bodyBuilder = new StringBuilder();

        // Добавляем часть с аудио файлом
        bodyBuilder.append("--").append(boundary).append("\r\n");
        bodyBuilder.append("Content-Disposition: form-data; name=\"audio_file\"; filename=\"")
                .append(fileName).append("\"\r\n");
        bodyBuilder.append("Content-Type: audio/ogg\r\n");
        bodyBuilder.append("\r\n");

        // Преобразуем в байты
        byte[] headerBytes = bodyBuilder.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
        byte[] footerBytes = ("\r\n--" + boundary + "--\r\n").getBytes(java.nio.charset.StandardCharsets.UTF_8);

        // Объединяем все части
        byte[] result = new byte[headerBytes.length + audioData.length + footerBytes.length];
        System.arraycopy(headerBytes, 0, result, 0, headerBytes.length);
        System.arraycopy(audioData, 0, result, headerBytes.length, audioData.length);
        System.arraycopy(footerBytes, 0, result, headerBytes.length + audioData.length, footerBytes.length);

        return result;
    }
}
