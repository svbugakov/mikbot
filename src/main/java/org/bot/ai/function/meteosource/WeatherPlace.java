package org.bot.ai.function.meteosource;

import com.fasterxml.jackson.annotation.JsonValue;

public enum WeatherPlace {
    MOSCOW("Москва", "55.7558", "37.6173"),
    SAINT_PETERSBURG("Санкт-Петербург", "59.9343", "30.3351"),
    NOVOSIBIRSK("Новосибирск", "55.0084", "82.9357"),
    LONDON("Лондон", "51.5074", "-0.1278"),
    NEW_YORK("Нью-Йорк", "40.7128", "-74.0060"),
    TOKYO("Токио", "35.6762", "139.6503"),
    LIPETSK("Липецк", "52.6122", "39.5981"),
    BELAIA_KALITVA ("Белая Калитва", "48.1773", "40.8021"),
    SARATOV ("Саратов", "51.5462", "46.0154");


    private final String location;
    private final String latitude;
    private final String longitude;

    WeatherPlace(String location, String latitude, String longitude) {
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @JsonValue
    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    // Метод для поиска по названию (case-insensitive)
    public static WeatherPlace fromLocation(String locationName) {
        for (WeatherPlace place : values()) {
            if (place.location.equalsIgnoreCase(locationName)) {
                return place;
            }
        }
        throw new IllegalArgumentException("Unknown location: " + locationName);
    }

    // Метод для получения координат в формате "lat,lon"
    public String getCoordinates() {
        return latitude + "," + longitude;
    }
}
