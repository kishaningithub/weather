package org.example;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class WeatherTest {
    @ParameterizedTest
    @NullAndEmptySource
    public void testCurrentTemperatureInCelsiusShouldReturnEmptyWhenLocationIsEmpty(String location) {
        Weather weather = new Weather(null, null);

        Optional<Integer> temperature = weather.currentTemperatureInCelsius(location);

        assertEquals(Optional.empty(), temperature);
    }

    @Test
    public void testCurrentTemperatureInCelsiusShouldReturnTemperatureInCelsiusWhenLocationIsGiven(){
        WeatherAPI weatherAPI = mock(WeatherAPI.class);
        Weather weather = new Weather(weatherAPI, Optional.empty());
        when(weatherAPI.getCurrentWeather("chennai")).thenReturn(new CurrentWeather(30));

        Optional<Integer> temperature = weather.currentTemperatureInCelsius("chennai");

        assertEquals(30, temperature.get());
    }

    @Test
    public void currentTemperatureInCelsiusShouldPersistTemperatureInformationWhenPostgresUrlIsSetInEnvironment() {
        WeatherAPI weatherAPI = mock(WeatherAPI.class);
        MeasurementRepository measurementRepository = mock(MeasurementRepository.class);
        Weather weather = new Weather(weatherAPI, Optional.of(measurementRepository));
        when(weatherAPI.getCurrentWeather("chennai")).thenReturn(new CurrentWeather(30));

        weather.currentTemperatureInCelsius("chennai");

        verify(measurementRepository).saveMeasurement(any(TemperatureMeasurement.class));
    }

}
