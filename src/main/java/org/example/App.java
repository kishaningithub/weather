package org.example;

import org.example.wttr.WttrClient;

import java.util.Optional;

public class App {
    public static void main(String[] args) {
        if(args.length == 0){
            return;
        }
        Weather weather = new Weather(new WttrClient("http://wttr.in"));
        Optional<Integer> temperature = weather.currentTemperatureInCelsius(args[0]);
        temperature.ifPresent(System.out::println);
    }
}
