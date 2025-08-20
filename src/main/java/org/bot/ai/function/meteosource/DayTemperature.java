package org.bot.ai.function.meteosource;

public class DayTemperature {
    private String morning;
    private String afternoon;
    private String evening;
    private String date;

    public DayTemperature() {
        this.morning = morning;
        this.afternoon = afternoon;
        this.evening = evening;
    }

    public String getAfternoon() {
        return afternoon;
    }

    public void setAfternoon(String afternoon) {
        this.afternoon = afternoon;
    }

    public String getEvening() {
        return evening;
    }

    public void setEvening(String evening) {
        this.evening = evening;
    }

    public String getMorning() {
        return morning;
    }

    public void setMorning(String morning) {
        this.morning = morning;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
