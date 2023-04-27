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

> We fake it till we make it!

yes you heard it right, this is the exact thought process which exactly what drives the design 
of the app from the ground up we call this "Test Driven Design"!.

So lets start faking!

For faking we will use the [mockito library](https://site.mockito.org/)!

```xml
<dependency>
  <groupId>org.mockito</groupId>
  <artifactId>mockito-core</artifactId>
  <version>5.2.0</version>
  <scope>test</scope>
</dependency>
```

```java
 @Test
public void testCurrentTemperatureInCelsiusShouldReturnTemperatureInCelsiusWhenLocationIsGiven(){
    WeatherAPI weatherAPI = mock(WeatherAPI.class);
    Weather weather = new Weather(weatherAPI);
    when(weatherAPI.getCurrentWeather("chennai")).thenReturn(new CurrentWeather(30));
    
    Optional<Integer> temperature = weather.currentTemperatureInCelsius("chennai");
    
    assertEquals(30, temperature.get());
}
```

If you look at the above we have created design through the use of mocks, what satisfies the above test in terms of design would be

```java
public interface WeatherAPI {
    CurrentWeather getCurrentWeather();
}
```

```java
public class CurrentWeather {
    private final int temperatureInCelsius;

    public CurrentWeather(int temperatureInCelsius) {
        this.temperatureInCelsius = temperatureInCelsius;
    }
}
```

Also if you observe the result we now have a pluggable unit "WeatherAPI" which the weather service interfaces with
this make the design open for extension and closed for modification which is the [open closed principle (OCP)](https://stackify.com/solid-design-open-closed-principle/)

now lets make the tests GREEN

```java
public Optional<Integer> currentTemperatureInCelsius(String location) {
    if(location == null || location.equals("")) {
        return Optional.empty();
    }
    CurrentWeather currentWeather = weatherAPI.getCurrentWeather(location);
    return Optional.of(currentWeather.getTemperatureInCelsius());
}
```

Ok now we have a working weather class, on to the API!


### Step 4: Create an API implementation

For this we need to choose a provider which will give us the current weather information.

For this project i have chosen this API which gives me the info I need and is simple, free and open source.

```bash
curl 'wttr.in/chennai?format=j1'
```

let's start with the test. For argument’s sake assume this api will charge us for every call and if we write a
test which directly this API and every team member and CI pipeline runs this our monthly bill is going to shoot up
so in order to address this concern and still have the safety net of automated tests we need a server which mimics 
wttr.in

for this purpose we will be using [wiremock](https://wiremock.org/)

so lets import it

```xml
<dependency>
  <groupId>com.github.tomakehurst</groupId>
  <artifactId>wiremock-jre8</artifactId>
  <version>2.35.0</version>
  <scope>test</scope>
</dependency>
```

and write the test which defines the design like before

```java
@WireMockTest
public class WttrClientTest {
    @Test
    public void fetchCurrentWeatherInfo(WireMockRuntimeInfo wmRuntimeInfo) {
        stubFor(get(urlPathEqualTo("/chennai"))
                .withQueryParam("format", equalTo("j1"))
                .willReturn(aResponse().withBodyFile("wttrResponse.json")));
        WttrClient client = new WttrClient(wmRuntimeInfo.getHttpBaseUrl());

        CurrentWeather actualWeather = client.getCurrentWeather("chennai");

        CurrentWeather expectedWeather = new CurrentWeather(28);
        assertEquals(expectedWeather, actualWeather);
    }
}
```

To pass this test we need to make a http call to the given URL. For that we will be using [unirest](https://kong.github.io/unirest-java/)

```java
public class WttrClient implements WeatherAPI {
    private final String baseUrl;

    public WttrClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public CurrentWeather getCurrentWeather(String location) {
        HttpResponse<WttrResponse> wttrResponse = Unirest.get(this.baseUrl + "/{location}")
                .routeParam("location", location)
                .queryString("format", "j1")
                .withObjectMapper(new JacksonObjectMapper())
                .asObject(WttrResponse.class);

        String temperatureInCelsius = wttrResponse.getBody().getCurrentCondition().get(0).getTemperatureInCelsius();

        return new CurrentWeather(Integer.parseInt(temperatureInCelsius));
    }
}
```

If you notice above i have also generated POJO classes to easily map the JSON response.

Now the test is GREEN! Let's REFACTOR!

The refactor i can think of is in terms of package structure as we now have significant code for wttr implementation where
i move wttr code into its specific package

```bash
$ tree
.
└── org
    └── example
        ├── CurrentWeather.java
        ├── Weather.java
        ├── WeatherAPI.java
        └── wttr
            ├── CurrentCondition.java
            ├── WttrClient.java
            └── WttrResponse.java

4 directories, 6 files
```