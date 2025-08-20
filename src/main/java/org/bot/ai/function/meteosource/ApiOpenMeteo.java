package org.bot.ai.function.meteosource;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class ApiOpenMeteo implements Weather {
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast";

    public String getWeather(
            String location,
            String latitude,
            String longitude,
            String startDate,
            String endDate
    ) throws Exception {
        String url = String.format("%s?latitude=%s&longitude=%s" +
                        "&hourly=temperature_2m" +
                        "&start_date=%s&end_date=%s" +
                        "&timezone=Europe/Moscow",
                API_URL, latitude, longitude, startDate, endDate);

        System.out.println(url);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException("Ошибка API: " + response.body());
        }

        return getTableDayPartsTemperatures(response.body(), location);
    }


    private String getTableDayPartsTemperatures(String jsonResponse, String location) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponse);
        JsonNode hourlyNode = rootNode.path("hourly");

        JsonNode timeNodes = hourlyNode.path("time");
        JsonNode tempNodes = hourlyNode.path("temperature_2m");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");


        StringBuilder answer = new StringBuilder();

        ObjectMapper mapper2 = new ObjectMapper();
        ObjectNode response = mapper2.createObjectNode();

        response.put("location", "Москва");
        response.putNull("error");

        response.put("location", location);
        response.putNull("error");


        final List<DayTemperature> daysTemperature = new ArrayList<>();
        DayTemperature dayTemperature = null;
        for (int i = 0; i < timeNodes.size(); i++) {
            String timeStr = timeNodes.get(i).asText();
            LocalDateTime dateTime = LocalDateTime.parse(timeStr, formatter);
            float temperature = tempNodes.get(i).floatValue();


            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String formattedDate = dateTime.format(formatter2);


            int hour = dateTime.getHour();
            if (hour == 13 || hour == 6 || hour == 19) {
                if (dayTemperature == null) {
                    dayTemperature = new DayTemperature();
                    dayTemperature.setDate(formattedDate);
                }
                String date = dateTime.toLocalDate().toString();
                if (hour == 6) {
                    dayTemperature.setMorning(String.valueOf(temperature));
                } else if (hour == 13) {
                    dayTemperature.setAfternoon(String.valueOf(temperature));
                } else if (hour == 19) {
                    dayTemperature.setEvening(String.valueOf(temperature));
                }
            }
            if (dayTemperature != null &&
                    dayTemperature.getMorning() != null
                    && dayTemperature.getAfternoon() != null
                    && dayTemperature.getEvening() != null
            ) {
                daysTemperature.add(dayTemperature);
                dayTemperature = null;
            }

        }

        for (final DayTemperature dayTemperature1 : daysTemperature) {
            ObjectNode forecast = mapper.createObjectNode();
            forecast.put("day", dayTemperature1.getDate());
            forecast.put("morning_temperature", dayTemperature1.getMorning());
            forecast.put("afternoon_temperature", dayTemperature1.getAfternoon());
            forecast.put("evening_temperature", dayTemperature1.getEvening());

            JsonNode node = response.get("forecasts");
            if (node != null) {
                ((ArrayNode) node).add(forecast);
            } else {
                response.putArray("forecasts").add(forecast);
            }
        }
        return mapper.writeValueAsString(response);
    }

}
