package org.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public interface HandlerMessage {
    String handle(String messageText, long chatId);

    SendPhoto handlePhoto(String messageText);

    boolean isApply(String messageText);

}
