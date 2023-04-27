package org.example;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeatherTest {
    @Test
    public void testCurrentTemperatureInCelsiusShouldReturnEmptyWhenLocationIsEmpty() {
        Weather weather = new Weather();

        Optional<Integer> temperature = weather.currentTemperatureInCelsius("");

        assertEquals(Optional.empty(), temperature);
    }
}
