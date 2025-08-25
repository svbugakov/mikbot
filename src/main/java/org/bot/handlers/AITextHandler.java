package org.bot.handlers;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.AIManager;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AITextHandler implements HandlerMessage{
    private static final Logger logger = LoggerFactory.getLogger(AITextHandler.class);

    private final AIManager aiManager;

    public AITextHandler(AIManager aiManager) {
        this.aiManager = aiManager;
    }

    @Override
    public ResponseAI handle(String messageText, long chatId) {
        // Путь к файлу (подставьте свой)
        messageText = messageText.replace("Мика", "");
        messageText = messageText.replace("Друг", "");
        ResponseAI response;
        try {
            logger.debug("query: " + messageText);
            response = aiManager.getResponse(messageText);
        } catch (Exception e) {
            logger.error("error in  AITextHandler:" , e);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }
        return response;
    }

    @Override
    public boolean isApply(String messageText) {
        return (messageText.startsWith("Мика") || messageText.startsWith("Друг")) && !messageText.contains("фото");
    }
}
