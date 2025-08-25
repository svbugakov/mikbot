package org.bot.ai.function.meteosource;

public class WeatherArgs {
    public WeatherPlace location;
    public WeatherDay day;
    public int shift;  // "today", "tomorrow", "2023-12-31", "next week"


    public WeatherArgs(WeatherPlace location, WeatherDay day, int shift) {
        this.location = location;
        this.day = day;
        this.shift = shift;
    }

    public WeatherArgs() {

    }
}
