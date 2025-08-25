package org.bot.handlers;

import org.bot.ai.entity.QuestionGoal;
import org.bot.ai.entity.ResponseAI;
import org.bot.ai.entity.StatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartHandler implements HandlerMessage{
    private static final Logger logger = LoggerFactory.getLogger(StartHandler.class);

    @Override
    public ResponseAI handle(String messageText, long chatId) {
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
