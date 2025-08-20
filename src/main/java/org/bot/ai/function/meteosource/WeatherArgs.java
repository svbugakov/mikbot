package org.bot.ai.function.meteosource;

public class WeatherArgs {
    public WeatherPlace location;
    public WeatherDay type;
    public int days;  // "today", "tomorrow", "2023-12-31", "next week"


    public WeatherArgs(WeatherPlace location, WeatherDay type, int days) {
        this.location = location;
        this.type = type;
        this.days = days;
    }

    public WeatherArgs() {

    }
}
