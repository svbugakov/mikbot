package org.bot.ai.function.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.*;
import org.bot.ai.function.AbstractAIWeatherFunction;
import org.bot.ai.function.TypeAI;
import org.bot.ai.function.TypeFunction;
import org.bot.ai.function.meteosource.WeatherArgs;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.ApiOpenMeteo;
import org.bot.ai.function.meteosource.WeatherPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class OpenAIWeatherFunction extends AbstractAIWeatherFunction<ChatFunction, ChatMessage, ChatFunctionCall> {
    private static final Logger logger = LoggerFactory.getLogger(OpenAIWeatherFunction.class);

    public OpenAIWeatherFunction(ApiOpenMeteo apiOpenMeteo) {
        super(apiOpenMeteo);
    }

    @Override
    public ChatFunction getFunc() {
        return ChatFunction.builder()
                .name(getName())
                .description("Получить погоду на сегодня, завтра, послезавтра, на конкретный день недели или период в указанном месте")
                .executor(WeatherArgs.class, this::logic)
                .build();
    }

    @Override
    public TypeFunction getTypeFunc() {
        return TypeFunction.WEATHER;
    }

    @Override
    public TypeAI getTypeAi() {
        return TypeAI.GPT;
    }

    @Override
    public ChatMessage getInstance(String role, String message, String numMessage) {
        return new ChatMessage(role, message);
    }

    @Override
    public ChatMessage getInstance(String role, ChatFunctionCall func, String numMessage) {
        return new ChatMessage(role, null, null, func);
    }

    @Override
    public ChatFunctionCall getInstanceFunc(String name, WeatherPlace place, WeatherDay weatherDay, int days) {
        return new ChatFunctionCall(name, jsonFromString(place, weatherDay, days));
    }

    private static JsonNode jsonFromString(
            WeatherPlace weatherPlace,
            WeatherDay weatherDay,
            int days
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            final String preparedStr = String.format("{\"location\":\"%s\",\"type\":\"%s\",\"days\":\"%d\"}",
                    weatherPlace.name(),
                    weatherDay.name(),
                    days);
            return mapper.readTree(preparedStr);
        } catch (Exception e) {
            throw new RuntimeException("Invalid JSON string", e);
        }
    }
}
