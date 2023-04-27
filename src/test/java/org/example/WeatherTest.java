package org.example;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class WeatherTest {
    @ParameterizedTest
    @NullAndEmptySource
    public void testCurrentTemperatureInCelsiusShouldReturnEmptyWhenLocationIsEmpty(String location) {
        Weather weather = new Weather();

        Optional<Integer> temperature = weather.currentTemperatureInCelsius(location);

        assertEquals(Optional.empty(), temperature);
    }

}
