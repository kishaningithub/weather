package org.example.wttr;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CurrentCondition {
    @JsonProperty("temp_C")
    private String temperatureInCelsius;

    public String getTemperatureInCelsius() {
        return temperatureInCelsius;
    }
}
