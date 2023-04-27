package org.example;

public class CurrentWeather {
    private final int temperatureInCelsius;

    public CurrentWeather(int temperatureInCelsius) {
        this.temperatureInCelsius = temperatureInCelsius;
    }

    public int getTemperatureInCelsius() {
        return temperatureInCelsius;
    }
}
