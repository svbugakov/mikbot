package org.bot.handlers;

import org.bot.Group;
import org.bot.ai.AIManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class StartHandler implements HandlerMessage{
    private static final Logger logger = LoggerFactory.getLogger(StartHandler.class);

    @Override
    public String handle(String messageText, long chatId) {
       String mes = """ 
               Привет! Я бот Мика.\n команды: /start /b.\n
               Для обращения к боту пишем в начале Мика, потом вопрос""";
       return mes;
    }

    @Override
    public SendPhoto handlePhoto(String messageText) {
        throw new UnsupportedOperationException("not to use handlePhoto for AITextHandler");
    }

    @Override
    public boolean isApply(String messageText) {
        return messageText.equals("/start");
    }
}
