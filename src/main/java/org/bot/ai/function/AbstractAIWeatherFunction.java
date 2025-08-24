package org.bot.ai.function;

import com.fasterxml.jackson.databind.JsonNode;
import com.theokanning.openai.completion.chat.*;
import org.bot.ai.ResponseAI;
import org.bot.ai.StatusResponse;
import org.bot.ai.function.meteosource.ApiOpenMeteo;
import org.bot.ai.function.meteosource.WeatherArgs;
import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractAIWeatherFunction<T, M, F> implements AIFunction<T, M, F> {
    private static final Logger logger = LoggerFactory.getLogger(AbstractAIWeatherFunction.class);
    private final ApiOpenMeteo apiOpenMeteo;

    public AbstractAIWeatherFunction(ApiOpenMeteo apiOpenMeteo) {
        this.apiOpenMeteo = apiOpenMeteo;
    }

    @Override
    public List<M> getTestMessages() {
        List<M> fewShotMessages = Arrays.asList(
                // Пример 1: Запрос на сегодня
                getInstance("system",
                        """
                                Ты можешь прогнозировать погоду у тебя для этого 
                                задана функция прогноза weather_forecast,
                                в аргумент location передавай название места латинскими буквами, например в случаи Липецка передавай LIPETSK
                                """,
                        "0"),
                getInstance("user", "Какая погода сегодня в Москве?", "1"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.MOSCOW, WeatherDay.TODAY, 0), "1"),


                // Пример 2: Запрос на конкретную дату
                getInstance("user", "Какая погода завтра в Липецке?", "2"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.LIPETSK, WeatherDay.TOMORROW, 0), "2"),


                getInstance("user", "Какая погода послезавтра в Саратове?", "3"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.SARATOV, WeatherDay.AFTER_TOMORROW, 0), "3"),


                getInstance("user", "Какая погода в четверг в Липецке?", "4"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.LIPETSK, WeatherDay.THURSDAY, 0), "4"),

                getInstance("user", "Какая погода в среду в Москве?", "5"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.MOSCOW, WeatherDay.WEDNESDAY, 0), "5"),

                getInstance("user", "Какая погода в среду в Белой Калитве?", "6"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.BELAIA_KALITVA, WeatherDay.WEDNESDAY, 0), "6"),

                getInstance("user", "Какая погода в ближайшие три дня в Липецке?", "7"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.LIPETSK, WeatherDay.PERIOD, 3), "7"),


                getInstance("user", "Какая погода в ближайшие 5 дней в Белой Калитве?", "8"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.BELAIA_KALITVA, WeatherDay.PERIOD, 5), "8"),


                getInstance("user", "Какая погода на неделю в Белой Калитве?", "9"),
                getInstance("assistant",
                        getInstanceFunc(getName(), WeatherPlace.BELAIA_KALITVA, WeatherDay.PERIOD, 7), "9")
        );
        return new ArrayList<>(fewShotMessages);
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


    @Override
    public String getName() {
        return "weather_forecast";
    }

    public abstract M getInstance(String role, String message, String numExample);

    public abstract M getInstance(String role, F func, String numExample);

    public abstract F getInstanceFunc(String name, WeatherPlace place, WeatherDay weatherDay, int days);
}
