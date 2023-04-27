package org.example;

import java.util.Optional;

public class Weather {

    private final WeatherAPI weatherAPI;

    public Weather(WeatherAPI weatherAPI) {
        this.weatherAPI = weatherAPI;
    }

    public Optional<Integer> currentTemperatureInCelsius(String location) {
        if(location == null || location.equals("")) {
            return Optional.empty();
        }
        CurrentWeather currentWeather = weatherAPI.getCurrentWeather(location);
        return Optional.of(currentWeather.getTemperatureInCelsius());
    }
}
