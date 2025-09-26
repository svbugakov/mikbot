package org.bot.handlers;

import com.google.api.services.drive.model.File;
import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.bot.google.FileManagerGDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class TextToImageHandler implements HandlerMessage<String> {
    private static final Logger logger = LoggerFactory.getLogger(TextToImageHandler.class);

    private FileManagerGDriver driver;

    public TextToImageHandler(FileManagerGDriver driver) {
        this.driver = driver;
    }

    @Override
    public ResponseAI handle(String messageText, Chat chat, int messageId) {
        ResponseAI responseAI;
        try {
            List<File> gFiles = driver.getFiles();
            Random random = new Random();
            int number = random.nextInt(gFiles.size());
            com.google.api.services.drive.model.File gFile = gFiles.get(number);

            responseAI = new ResponseAI(
                    StringUtils.EMPTY,
                    StatusResponse.SUCCESS,
                    driver.downloadFileAsBytes(gFile.getId()),
                    null,
                    QuestionGoal.PICTURE
            );
        } catch (IOException e) {
            logger.error("error in getting file:", e);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }
        return responseAI;
    }

    @Override
    public boolean isApply(String messageText) {
        return (messageText.startsWith("Мика") || messageText.startsWith("Друг")) && messageText.contains("фото");
    }
}
