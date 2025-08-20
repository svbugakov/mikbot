package org.bot.handlers;

import org.bot.ai.AIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class AITextHandler implements HandlerMessage{
    private static final Logger logger = LoggerFactory.getLogger(AITextHandler.class);

    private final AIManager aiManager;

    public AITextHandler(AIManager aiManager) {
        this.aiManager = aiManager;
    }

    @Override
    public String handle(String messageText, long chatId) {
        // Путь к файлу (подставьте свой)
        messageText = messageText.replace("Мика", "");
        messageText = messageText.replace("Друг", "");
        String response;
        try {
            logger.debug("query: " + messageText);
            response = aiManager.getResponse(messageText);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    @Override
    public SendPhoto handlePhoto(String messageText) {
        throw new UnsupportedOperationException("not to use handlePhoto for AITextHandler");
    }

    @Override
    public boolean isApply(String messageText) {
        return (messageText.startsWith("Мика") || messageText.startsWith("Друг")) && !messageText.contains("фото");
    }
}
