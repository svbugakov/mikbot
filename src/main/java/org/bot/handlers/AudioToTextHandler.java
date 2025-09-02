package org.bot.handlers;


import org.apache.commons.lang3.StringUtils;
import org.bot.ai.AIManager;
import org.bot.ai.entity.ByteAndTextQuestion;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Voice;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class AudioToTextHandler implements HandlerMessage<Voice> {
    private static final Logger logger = LoggerFactory.getLogger(AudioToTextHandler.class);
    private final AIManager aiManager;
    private final TelegramLongPollingBot telegramLongPollingBot;
    private String token;

    public AudioToTextHandler(AIManager aiManager, String token, TelegramLongPollingBot telegramLongPollingBot) {
        this.aiManager = aiManager;
        this.telegramLongPollingBot = telegramLongPollingBot;
        this.token = token;
    }

    @Override
    public ResponseAI handle(Voice voice, Chat chat, int messageId) {
        byte[] audio;
        try {
            audio = downloadVoiceMessage(voice);
        } catch (Exception e) {
            logger.error("error in getting voice content", e);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }
        ByteAndTextQuestion questionGoal =
                new ByteAndTextQuestion(
                        StringUtils.EMPTY,
                        QuestionGoal.AUDIO,
                        audio
                );
        ResponseAI responseAI = aiManager.getResponse(questionGoal);
        if (responseAI.getStatus() == StatusResponse.SUCCESS) {
            deleteMessage(chat.getId(), messageId);
            sendMessageWithSignature(chat, responseAI.getResponse());
        }
        return responseAI;
    }

    @Override
    public TypeMessage getType() {
        return TypeMessage.AUDIO;
    }

    @Override
    public boolean isApply(Voice messageText) {
        return true;
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId.toString());
        deleteMessage.setMessageId(messageId);

        try {
            telegramLongPollingBot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            logger.error("Failed to delete message: ", e);
        }
    }

    private byte[] downloadVoiceMessage(Voice voice) throws Exception {
        // Получаем информацию о файле
        GetFile getFile = new GetFile();
        getFile.setFileId(voice.getFileId());

        org.telegram.telegrambots.meta.api.objects.File file;
        try {
            file = telegramLongPollingBot.execute(getFile);
        } catch (TelegramApiException e) {
            throw new Exception("Failed to get file info", e);
        }

        // Формируем URL для скачивания
        String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + file.getFilePath();
        String fileExtension = getFileExtension(file.getFilePath());
        String fileName = "voice_" + System.currentTimeMillis() + fileExtension;

        // Скачиваем файл
        Path tempFile = Files.createTempFile("telegram_voice_", fileExtension);
        try (InputStream in = new URL(fileUrl).openStream()) {
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return Files.readAllBytes(tempFile.toFile().toPath());
    }

    private String getFileExtension(String filePath) {
        if (filePath != null && filePath.contains(".")) {
            return filePath.substring(filePath.lastIndexOf("."));
        }
        return ".ogg"; // По умолчанию для голосовых сообщений
    }

    public void sendMessageWithSignature(Chat chat, String text) {
        String signedText = text + "\n\n<code>╰ from %s</code>".formatted(chat.getUserName());

        SendMessage message = new SendMessage();
        message.setChatId(chat.getId().toString());
        message.setText(signedText);
        message.setParseMode("HTML");

        try {
            telegramLongPollingBot.execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
