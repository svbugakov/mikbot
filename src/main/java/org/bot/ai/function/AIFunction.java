package org.bot.ai.function;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatFunction;
import com.theokanning.openai.completion.chat.ChatFunctionCall;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import org.bot.ai.function.meteosource.WeatherArgs;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public interface AIFunction<T, M, F> {
    T getFunc();

    List<M> getTestMessages();

    TypeFunction getTypeFunc();

    String getName();

    String logic( final Map<String, Object> args);

    default boolean isApply(String name) {
        return getName().equals(name);
    }

    TypeAI getTypeAi();
}
