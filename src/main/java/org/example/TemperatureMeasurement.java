package org.example;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemperatureMeasurement {
    private ZonedDateTime measurementTime;
    private Integer temperatureInCelsius;
}
