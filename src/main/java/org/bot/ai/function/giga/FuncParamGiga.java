package org.bot.ai.function.giga;

import org.bot.ai.function.meteosource.WeatherDay;
import org.bot.ai.function.meteosource.WeatherPlace;

public class FuncParamGiga {
    private String name;
    private WeatherPlace place;
    private WeatherDay weatherDay;
    private int days;

    public FuncParamGiga(String name, WeatherPlace place, WeatherDay weatherDay, int days) {
        this.name = name;
        this.place = place;
        this.weatherDay = weatherDay;
        this.days = days;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public WeatherPlace getPlace() {
        return place;
    }

    public void setPlace(WeatherPlace place) {
        this.place = place;
    }

    public WeatherDay getWeatherDay() {
        return weatherDay;
    }

    public void setWeatherDay(WeatherDay weatherDay) {
        this.weatherDay = weatherDay;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }
}
