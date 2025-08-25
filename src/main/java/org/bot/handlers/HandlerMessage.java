package org.bot.handlers;

import org.bot.ai.entity.ResponseAI;

public interface HandlerMessage {
    ResponseAI handle(String messageText, long chatId);

    boolean isApply(String messageText);

}
