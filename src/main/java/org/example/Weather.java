package org.example;

import java.time.ZonedDateTime;
import java.util.Optional;

public class Weather {

    private final WeatherAPI weatherAPI;
    private final Optional<MeasurementRepository> measurementRepository;

    public Weather(WeatherAPI weatherAPI, Optional<MeasurementRepository> measurementRepository) {
        this.weatherAPI = weatherAPI;
        this.measurementRepository = measurementRepository;
    }

    public Optional<Integer> currentTemperatureInCelsius(String location) {
        if (location == null || location.equals("")) {
            return Optional.empty();
        }
        CurrentWeather currentWeather = weatherAPI.getCurrentWeather(location);
        this.measurementRepository.ifPresent(repo -> {
            repo.saveMeasurement(new TemperatureMeasurement(ZonedDateTime.now(), currentWeather.getTemperatureInCelsius()));
        });
        return Optional.of(currentWeather.getTemperatureInCelsius());
    }

}
