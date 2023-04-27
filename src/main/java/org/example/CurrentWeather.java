package org.example;

import java.util.Objects;

public class CurrentWeather {
    private final int temperatureInCelsius;

    public CurrentWeather(int temperatureInCelsius) {
        this.temperatureInCelsius = temperatureInCelsius;
    }

    public int getTemperatureInCelsius() {
        return temperatureInCelsius;
    }

    @Override
    public String toString() {
        return "CurrentWeather{" +
                "temperatureInCelsius=" + temperatureInCelsius +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CurrentWeather that = (CurrentWeather) o;
        return temperatureInCelsius == that.temperatureInCelsius;
    }

    @Override
    public int hashCode() {
        return Objects.hash(temperatureInCelsius);
    }
}
