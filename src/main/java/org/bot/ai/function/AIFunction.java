package org.bot.ai.function;


import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatMessage;
import org.bot.ai.function.meteosource.WeatherArgs;

import java.util.List;

public interface AIFunction {
    ChatFunction getFunc();

    List<ChatMessage> getTestMessages();

    TypeFunction getTypeFunc();

    String getName();

    String logic(final WeatherArgs weatherArgs);

    default boolean isApply(String name) {
        return getName().equals(name);
    }
}
