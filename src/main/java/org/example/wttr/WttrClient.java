package org.example.wttr;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import kong.unirest.jackson.JacksonObjectMapper;
import org.example.CurrentWeather;
import org.example.WeatherAPI;

public class WttrClient implements WeatherAPI {
    private final String baseUrl;

    public WttrClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public CurrentWeather getCurrentWeather(String location) {
        HttpResponse<WttrResponse> wttrResponse = Unirest.get(this.baseUrl + "/{location}")
                .routeParam("location", location)
                .queryString("format", "j1")
                .withObjectMapper(new JacksonObjectMapper())
                .asObject(WttrResponse.class);

        String temperatureInCelsius = wttrResponse.getBody().getCurrentCondition().get(0).getTemperatureInCelsius();

        return new CurrentWeather(Integer.parseInt(temperatureInCelsius));
    }
}
