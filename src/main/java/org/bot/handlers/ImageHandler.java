package org.bot.handlers;

import com.google.api.services.drive.model.File;
import org.apache.commons.lang3.StringUtils;
import org.bot.ai.ResponseAI;
import org.bot.ai.QuestionGoal;
import org.bot.ai.StatusResponse;
import org.bot.gdrive.FileManagerGDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ImageHandler implements HandlerMessage {
    private static final Logger logger = LoggerFactory.getLogger(ImageHandler.class);

    private FileManagerGDriver driver;

    public ImageHandler(FileManagerGDriver driver) {
        this.driver = driver;
    }

    @Override
    public ResponseAI handle(String messageText, long chatId) {
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
