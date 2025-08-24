package org.bot.handlers;

import org.bot.ai.ResponseAI;

public interface HandlerMessage {
    ResponseAI handle(String messageText, long chatId);

    boolean isApply(String messageText);

}
