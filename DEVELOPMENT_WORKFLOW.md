# Development process

This weather app was built completely following the TDD process. If you directly jumped here, kindly read
about the [development workflow of factorial app](https://github.com/kishaningithub/factorial/blob/main/DEVELOPMENT_PROCESS.md)
which is a prerequisite for this one.


## Requirement 1

Build a weather cli app which upon receiving the name of the city should give the current temperature in Celsius.

As usual, lets start with a test!

### Step 1: Start with the boundary case (i.e. where a location is not given)

this is how it looks after the RED, GREEN, REFACTOR cycle.

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

### Step 2: Boundary case 2 - location is null

this is how it looks after the RED, GREEN, REFACTOR cycle.

```java
public class WeatherTest {
    @ParameterizedTest
    @NullAndEmptySource
    public void testCurrentTemperatureInCelsiusShouldReturnEmptyWhenLocationIsEmpty(String location) {
        Weather weather = new Weather();

        Optional<Integer> temperature = weather.currentTemperatureInCelsius(location);

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

### Step 3: When valid location is given

```java
@Test
public void testCurrentTemperatureInCelsiusShouldReturnTemperatureInCelsiusWhenLocationIsGiven(){
    Weather weather = new Weather();

    Optional<Integer> temperature = weather.currentTemperatureInCelsius("chennai");

    assertEquals(30, temperature.get());
}
```

The above test will go to RED with the following error 

```java
java.util.NoSuchElementException: No value present
```

Now here comes the implementation thought process, for the `currentTemperatureInCelsius` method to return the temperature
value it should talk to an API which gives it that value, we actually dont have the API yet so what do we do? 

> we fake it till we make it!

yes you heard it right, this is the exact thought process which exactly what drives the design 
of the app from the ground up we call this "Test Driven Design"!.

So lets start faking!
