package org.bot.ai.function.meteosource;

import java.util.List;

public enum WeatherDay {
    TODAY(0),
    TOMORROW(1),
    AFTER_TOMORROW(2),
    MONDAY(0),
    TUESDAY(0),
    WEDNESDAY(0),
    THURSDAY(0),
    FRIDAY(0),
    SATURDAY(0),
    SUNDAY(0),
    PERIOD(0);


    private final Integer day;

    WeatherDay(Integer day) {
        this.day = day;
    }

    public Integer getDay() {
        return day;
    }

    public static List<WeatherDay> todayDays = List.of(TODAY, TOMORROW, AFTER_TOMORROW);
    public static List<WeatherDay> daysOfWeek = List.of(MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY);
}
