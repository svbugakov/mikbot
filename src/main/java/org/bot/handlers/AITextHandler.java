package org.bot.handlers;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.AIManager;
import org.bot.ai.entity.SimpleQuestion;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class AITextHandler implements HandlerMessage<String> {
    private static final Logger logger = LoggerFactory.getLogger(AITextHandler.class);

    private final AIManager aiManager;

    public AITextHandler(AIManager aiManager) {
        this.aiManager = aiManager;
    }

    @Override
    public ResponseAI handle(String messageText, Chat chat, int messageId) {
        // Путь к файлу (подставьте свой)
        messageText = messageText.replace("Мика", "");
        messageText = messageText.replace("Друг", "");
        ResponseAI response;
        try {
            logger.debug("query: " + messageText);
            SimpleQuestion questionGoal = new SimpleQuestion(messageText, QuestionGoal.TEXT);
            response = aiManager.getResponse(questionGoal);
        } catch (Exception e) {
            logger.error("error in  AITextHandler:", e);
            return new ResponseAI(StringUtils.EMPTY, StatusResponse.FAILED);
        }
        return response;
    }

    @Override
    public boolean isApply(String messageText) {
        return (messageText.startsWith("Мика") || messageText.startsWith("Друг")) && !messageText.contains("фото");
    }
}
