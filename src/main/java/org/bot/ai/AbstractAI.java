package org.bot.ai;

import com.fasterxml.jackson.databind.JsonNode;
import org.bot.ai.function.AIFunction;
import org.bot.ai.function.meteosource.WeatherArgs;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public interface AbstractAI {
    Logger logger = LoggerFactory.getLogger(AbstractAI.class);

    String getName();

    String getApiKey();

    String getModel();

    String getRequestModel();

    String getUri();

    ResponseAI getResponse(Question question);

    default String aiFuncLogic(
            final Map<String, Object> jsonNode,
            final AIFunction aiFunction
    ) {
        WeatherArgs weatherArgs = new WeatherArgs(
                WeatherPlace.valueOf(jsonNode.get("location").toString()),
                WeatherDay.valueOf(jsonNode.get("day").toString()),
                Integer.parseInt(jsonNode.get("shift").toString())
        );

        String responseWeather = null;
        try {
            responseWeather = aiFunction.logic(
                    weatherArgs
            );
        } catch (Exception e) {
            logger.error("error in call aiFunction.logic", e);
            throw new RuntimeException(e);
        }

        return responseWeather;
    }
}
