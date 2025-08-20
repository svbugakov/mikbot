package org.bot.ai.function.openai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.*;
import org.bot.ai.DeepSeekWebClient;
import org.bot.ai.function.AIFunction;
import org.bot.ai.function.TypeFunction;
import org.bot.ai.function.meteosource.WeatherArgs;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;
import org.bot.ai.function.meteosource.ApiOpenMeteo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OpenAIFunctionWeather implements AIFunction {

    private static final Logger logger = LoggerFactory.getLogger(OpenAIFunctionWeather.class);

    private final ApiOpenMeteo apiOpenMeteo;

    public OpenAIFunctionWeather() {
        this.apiOpenMeteo = new ApiOpenMeteo();
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
    public List<ChatMessage> getTestMessages() {
        List<ChatMessage> fewShotMessages = Arrays.asList(
                // Пример 1: Запрос на сегодня
                new ChatMessage(ChatMessageRole.SYSTEM.value(), "Ты можешь отвечать на общие вопросы, а также " +
                        "неплохо прогнозировать погоду у тебя для этого " +
                        "задана функция прогноза weather_forecast, в аргумент location передавай название места всегда на eng"),
                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода сегодня в Москве?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.MOSCOW, WeatherDay.TODAY, 0))),


                // Пример 2: Запрос на конкретную дату
                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода завтра в Липецке?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.LIPETSK, WeatherDay.TOMORROW, 0))),


                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода послезавтра в Саратове?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.SARATOV, WeatherDay.AFTER_TOMORROW, 0))),


                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода в четверг в Липецке?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.LIPETSK, WeatherDay.THURSDAY, 0))),

                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода в среду в Москве?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.MOSCOW, WeatherDay.WEDNESDAY, 0))),

                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода в среду в Белой Калитве?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.BELAIA_KALITVA, WeatherDay.WEDNESDAY, 0))),

                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода в ближайшие три дня в Липецке?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.LIPETSK, WeatherDay.PERIOD, 3))),


                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода в ближайшие 5 дней в Белой Калитве?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.BELAIA_KALITVA, WeatherDay.PERIOD, 5))),


                new ChatMessage(ChatMessageRole.USER.value(), "Какая погода на неделю в Белой Калитве?"),
                new ChatMessage(ChatMessageRole.ASSISTANT.value(), null, null,
                        new ChatFunctionCall(getName(), jsonFromString(WeatherPlace.BELAIA_KALITVA, WeatherDay.PERIOD, 7)))
        );
        return new ArrayList<>(fewShotMessages);
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
    public String logic(WeatherArgs weatherArgs) {
        LocalDate currentDate = LocalDate.now();
        LocalDate futureDate = null;

        // Добавляем n дней
        WeatherDay weatherDay = weatherArgs.type;
        if (WeatherDay.daysOfWeek.contains(weatherDay)) {
            DayOfWeek targetDay = DayOfWeek.valueOf(weatherDay.name().toUpperCase());
            futureDate = currentDate.with(TemporalAdjusters.nextOrSame(targetDay));
            currentDate = futureDate;
        } else if (WeatherDay.todayDays.contains(weatherDay)) {
            futureDate = currentDate.plusDays(weatherDay.getDay());
            currentDate = futureDate;
        } else if (weatherDay == WeatherDay.PERIOD) {
            futureDate = currentDate.plusDays(weatherArgs.days);
        } else {
            throw new IllegalArgumentException("Unknow WeatherDay!" + weatherDay.name());
        }

        // Форматируем вывод (опционально)
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        String weather;
        try {
            weather = apiOpenMeteo.getWeather(
                    weatherArgs.location.name(),
                    weatherArgs.location.getLatitude(),
                    weatherArgs.location.getLongitude(),
                    currentDate.format(formatter),
                    futureDate.format(formatter)
            );
            System.out.println(weather);
        } catch (Exception e) {
           logger.error("error in call getWeather: ", e);
           throw new RuntimeException(e);
        }

        return weather;
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
