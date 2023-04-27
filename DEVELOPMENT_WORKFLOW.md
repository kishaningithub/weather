# Development process

This weather app was built completely following the TDD process. If you directly jumped here, kindly read
about the [development workflow of factorial app](https://github.com/kishaningithub/factorial/blob/main/DEVELOPMENT_PROCESS.md)
which is a prerequisite for this one.


## Requirement 1

Build a weather cli app which upon receiving the name of the city should give the current temperature in Celsius.

As usual, lets start with a test!

Step 1: Start with the boundary case (i.e. where a location is not given), this is how it looks after the RED, GREEN, REFACTOR cycle.

```java
public class WeatherTest {
    @Test
    public void testCurrentTemperatureInCelsiusShouldReturnEmptyWhenLocationIsEmpty() {
        Weather weather = new Weather();

        Optional<Integer> temperature = weather.currentTemperatureInCelsius("");

        assertEquals(Optional.empty(), temperature);
    }
}
```

```java
public class Weather {

    public Optional<Integer> currentTemperatureInCelsius(String location) {
        return Optional.empty();
    }
}
```


