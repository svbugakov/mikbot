package org.bot.ai.function.giga;

import chat.giga.model.completion.*;

import org.apache.commons.lang3.StringUtils;
import org.bot.ai.function.AbstractAIWeatherFunction;
import org.bot.ai.function.TypeAI;
import org.bot.ai.function.TypeFunction;
import org.bot.ai.function.meteosource.ApiOpenMeteo;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class GigaWeatherFunction extends AbstractAIWeatherFunction<ChatFunction, ChatFunctionFewShotExample, FuncParamGiga> {

    private static final Logger logger = LoggerFactory.getLogger(GigaWeatherFunction.class);
    private String goal;
    private final List<ChatFunctionFewShotExample> shots;

    public GigaWeatherFunction(ApiOpenMeteo apiOpenMeteo) {
        super(apiOpenMeteo);
        shots =
                getTestMessages().stream()
                        .filter(gigaShot -> gigaShot.params() != null
                                && !gigaShot.params().isEmpty())
                        .toList();
        if (StringUtils.isEmpty(goal)) {
            throw new IllegalStateException("not defined descr goal of GigaFunctionWeather!");
        }
    }

    @Override
    public ChatFunction getFunc() {
        return ChatFunction.builder()
                .name(getName())
                .description(goal)
                .parameters(ChatFunctionParameters.builder()
                        .type("object")
                        .properties(Map.of(
                                "location", ChatFunctionParametersProperty.builder()
                                        .type("string")
                                        .description("Местоположение, например, название города")
                                        .build(),
                                "day", ChatFunctionParametersProperty.builder()
                                        .type("string")
                                        .description("День или период")
                                        .build(),
                                "shift", ChatFunctionParametersProperty.builder()
                                        .type("integer")
                                        .description("Кол-во дней для периода")
                                        .build()))
                        .required(List.of("location", "day", "shift"))
                        .build())
                .fewShotExamples(shots)
                .build();
    }

    @Override
    public TypeFunction getTypeFunc() {
        return TypeFunction.WEATHER;
    }

    @Override
    public String getName() {
        return "weather_forecast";
    }

    @Override
    public TypeAI getTypeAi() {
        return TypeAI.GIGA;
    }

    final Map<String, String> testMessages = new HashMap<>();

    @Override
    public ChatFunctionFewShotExample getInstance(String role, String message, String numExample) {
        if (role.equals("system")) {
            goal = message;
        }
        testMessages.put(numExample, message);
        return ChatFunctionFewShotExample.builder()
                .request(message)
                .build();
    }

    @Override
    public ChatFunctionFewShotExample getInstance(String role, FuncParamGiga func, String numExample) {
        return ChatFunctionFewShotExample.builder()
                .request(testMessages.get(numExample))
                .param("location", func.getPlace())
                .param("day", func.getWeatherDay())
                .param("shift", func.getDays())
                .build();
    }

    @Override
    public FuncParamGiga getInstanceFunc(String name, WeatherPlace place, WeatherDay weatherDay, int days) {
        return new FuncParamGiga(name, place, weatherDay, days);
    }

}
