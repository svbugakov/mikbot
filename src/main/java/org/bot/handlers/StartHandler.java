package org.bot.handlers;

import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Chat;

public class StartHandler implements HandlerMessage<String>{
    private static final Logger logger = LoggerFactory.getLogger(StartHandler.class);

    @Override
    public ResponseAI handle(String messageText, Chat chat, int messageId) {
       String mes = """ 
               Привет! Я бот Мика.\n команды: /start /b.\n
               Для обращения к боту пишем в начале Мика, потом вопрос""";
       return new ResponseAI(mes, StatusResponse.SUCCESS, QuestionGoal.TEXT);
    }

    @Override
    public boolean isApply(String messageText) {
        return messageText.equals("/start");
    }
}
