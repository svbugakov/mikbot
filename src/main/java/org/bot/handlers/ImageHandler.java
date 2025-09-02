package org.bot.handlers;

import org.bot.FriendsLip;
import org.bot.ai.AIManager;
import org.bot.ai.entity.ByteAndTextQuestion;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.File;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

public class ImageHandler implements HandlerMessage<List<PhotoSize>> {
    private static final Logger logger = LoggerFactory.getLogger(FriendsLip.class);
    private final TelegramLongPollingBot telegramLongPollingBot;
    private final String token;
    private final AIManager aiManager;

    public ImageHandler(TelegramLongPollingBot telegramLongPollingBot, String token, AIManager aiManager) {
        this.telegramLongPollingBot = telegramLongPollingBot;
        this.token = token;
        this.aiManager = aiManager;
    }

    @Override
    public ResponseAI handle(List<PhotoSize> message, Chat chat, int messageId) {
        byte[] bytes = getImage(message);
        ByteAndTextQuestion questionGoal =
                new ByteAndTextQuestion(
                        "Вытащи весь текс и цифры из фото",
                        QuestionGoal.BYTE_TEXT,
                        bytes
                );
        ResponseAI responseAI = aiManager.getResponse(questionGoal);
        return responseAI;
    }

    @Override
    public boolean isApply(List<PhotoSize> messageText) {
        return false;
    }

    @Override
    public TypeMessage getType() {
        return TypeMessage.IMAGE;
    }

    private byte[] getImage(List<PhotoSize> photos) {
        // Получаем фото с наибольшим разрешением (последнее в списке)
        PhotoSize fistPhoto = photos.stream()
                .max(Comparator.comparing(PhotoSize::getFileSize))
                .orElseThrow(() -> new RuntimeException("No photos found"));

        // Получаем информацию о файле
        String fileId = fistPhoto.getFileId();
        GetFile getFile = new GetFile();
        getFile.setFileId(fileId);
        byte[] bytes = null;
        try {
            File file = telegramLongPollingBot.execute(getFile);
            bytes = downloadFileBytes(file);
        } catch (Exception e) {
            logger.error("error in getting image", e);
        }
        return bytes;
    }

    // Метод для скачивания файла в byte[]
    private byte[] downloadFileBytes(File file) throws IOException {
        String fileUrl = "https://api.telegram.org/file/bot" + token + "/" + file.getFilePath();

        try (InputStream in = new URL(fileUrl).openStream()) {
            return in.readAllBytes();
        }
    }
}
