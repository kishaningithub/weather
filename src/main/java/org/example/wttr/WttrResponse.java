package org.example.wttr;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class WttrResponse {


    @JsonProperty("current_condition")
    private List<CurrentCondition> currentCondition;


    public List<CurrentCondition> getCurrentCondition() {
        return currentCondition;
    }
}
