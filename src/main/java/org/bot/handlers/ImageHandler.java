package org.bot.handlers;

import com.google.api.services.drive.model.File;
import org.bot.ai.AIManager;
import org.bot.gdrive.FileManagerGDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Random;

public class ImageHandler implements HandlerMessage {
    private static final Logger logger = LoggerFactory.getLogger(ImageHandler.class);

    private FileManagerGDriver driver;

    public ImageHandler(FileManagerGDriver driver) {
        this.driver = driver;
    }

    @Override
    public String handle(String messageText, long chatId) {
        throw new UnsupportedOperationException("not to use handle for ImageHandler");
    }

    @Override
    public SendPhoto handlePhoto(String messageText) {
        SendPhoto photo = new SendPhoto();
        try {
            List<File> gFiles = driver.getFiles();
            Random random = new Random();
            int number = random.nextInt(gFiles.size());
            com.google.api.services.drive.model.File gFile = gFiles.get(number);

            InputStream inputStream = new ByteArrayInputStream(driver.downloadFileAsBytes(gFile.getId()));
            InputFile inputFile = new InputFile();
            inputFile.setMedia(inputStream, gFile.getName());// URL или File
            photo.setPhoto(inputFile);
        } catch (IOException e) {
            logger.error("error in getting file:", e);
        }
        return photo;
    }

    @Override
    public boolean isApply(String messageText) {
        return (messageText.startsWith("Мика") || messageText.startsWith("Друг")) && messageText.contains("фото");
    }
}
