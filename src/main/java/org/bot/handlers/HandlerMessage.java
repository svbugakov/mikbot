package org.bot.handlers;

import org.bot.ai.entity.ResponseAI;
import org.telegram.telegrambots.meta.api.objects.Chat;

public interface HandlerMessage<T> {
    ResponseAI handle(T message, Chat chatId, int messageId);

    boolean isApply(T messageText);

    default boolean isApplyType(TypeMessage typeMessage) {
        return typeMessage == getType();
    }

    default TypeMessage getType() {
        return TypeMessage.TEXT;
    }
}
