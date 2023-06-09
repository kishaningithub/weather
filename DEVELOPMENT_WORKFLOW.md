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

The refactor I can think of is in terms of package structure as we now have significant code for wttr implementation where
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

### Step 5: Create the CLI interface

Let's start with a test as usual

```java
@Test
public void testCliInterfaceShouldGiveTheTemperatureForTheGivenLocation() {
    ByteArrayOutputStream result = new ByteArrayOutputStream();
    System.setOut(new PrintStream(result));

    App.main(new String[]{"chennai"});

    int actualTemperature = Integer.parseInt(result.toString().trim());
    assertTrue(actualTemperature >= 20 && actualTemperature <= 55 );
}
```

make the test pass

```java
public class App {
    public static void main(String[] args) {
        Weather weather = new Weather(new WttrClient("http://wttr.in"));

        Optional<Integer> temperature = weather.currentTemperatureInCelsius(args[0]);

        temperature.ifPresent(System.out::println);
    }
}
```

if you notice above we are doing `args[0]` yuck! what if user did not pass any arg? lets handle that

```java
@Test
public void testCliInterfaceShouldNotFailIfNoInputIsGiven() {
    assertDoesNotThrow(() -> App.main(new String[]{}));
}
```

```java
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
```

If you noticed about we have 3 different types of tests

1. Unit tests (`WeaterTest.java`)
2. Integration tests (`WttrClientTest.java`)
3. End-To-End tests (`AppTest.java`)

These 3 form what is called the [test pyramid](https://martinfowler.com/bliki/TestPyramid.html)

```
      ╱╲
  End-to-End
    ╱────╲
   ╱ Inte-╲
  ╱ gration╲
 ╱──────────╲
╱   Unit     ╲
──────────────
```

## Requirement 2 - Store the results in postgres if enabled via environment

So far we have been doing outside in tdd (mostly) now we will do inside out tdd.

here we will first create a measurementRepository layer.

To write the tests for this we need a running postgres instance but installing postgres on the system where the
tests are running beforehand will lead to complexities for the development team and also for the CI pipelines.

So we will leverage the power of [testcontainers](https://www.testcontainers.org/) library which in turn leverages the
power of [docker](https://www.docker.com/). For connecting to database we will be using [jdbi](https://jdbi.org/)

### Step 1 - Create a repository for storing current temperature measurements

So as always lets start with a test

```java
@Testcontainers
public class MeasurementRepositoryTest {

    @Container
    public PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:15.2-alpine"));

    @Test
    public void saveMeasurementShouldPersistCurrentTemperatureWithTime() throws InterruptedException {
        Jdbi jdbi = Jdbi.create(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
        jdbi.useHandle(handle -> handle.execute("create table temperature_measurements(measurement_time timestamptz, temperature_in_celsius integer)"));
        TemperatureMeasurement measurement = new TemperatureMeasurement(ZonedDateTime.now(), 30);
        MeasurementRepository measurementRepository = new MeasurementRepository(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());

        measurementRepository.saveMeasurement(measurement);

        jdbi.useHandle(handle -> {
            TemperatureMeasurement actual = handle.createQuery("select * from temperature_measurements")
                    .mapToBean(TemperatureMeasurement.class)
                    .first();
            assertEquals(measurement, actual);
        });
    }
}
```

let's make it GREEN and refactor

```java
public class MeasurementRepository {

    private final Jdbi jdbi;

    public MeasurementRepository(String jdbcUrl, String username, String password) {
        this.jdbi = Jdbi.create(jdbcUrl, username, password);
    }

    public void saveMeasurement(TemperatureMeasurement measurement) {
        jdbi.useHandle(handle -> handle
                .createUpdate("insert into temperature_measurements(measurement_time, temperature_in_celsius) values (:measurementTime, :temperatureInCelsius)")
                .bindBean(measurement)
                .execute());
    }
}
```

### Step 2 - Wire it up

If you noticed we have developed a new feature without touching the rest of the application, this of this like a LEGO block
so now all you have to do is plug this lego into the application circuit. This is one of the benefits of TDD, it makes
the code modular.

The wire up happens in the orchestrator which happens to be the `Weather` class, below is the test
```java
@Test
public void currentTemperatureInCelsiusShouldPersistTemperatureInformationWhenPostgresUrlIsSetInEnvironment() {
    WeatherAPI weatherAPI = mock(WeatherAPI.class);
    MeasurementRepository measurementRepository = mock(MeasurementRepository.class);
    Function<String, MeasurementRepository> measurementRepositorySupplier = (String jdbcUrl) -> measurementRepository;
    Weather weather = new Weather(weatherAPI, measurementRepositorySupplier);
    when(weatherAPI.getCurrentWeather("chennai")).thenReturn(new CurrentWeather(30));
    System.setProperty("POSTGRES_JDBC_URL", "someURL");

    weather.currentTemperatureInCelsius("chennai");

    verify(measurementRepository).saveMeasurement(any(TemperatureMeasurement.class));
}
```

if you notice above the test has nudged us to make the dependency to measurement repository a lazy one i.e. we will only
construct this dependency if `POSTGRES_JDBC_URL` environment variable is set, below is the implementation for the above
test

```java
public Optional<Integer> currentTemperatureInCelsius(String location) {
    if(location == null || location.equals("")) {
        return Optional.empty();
    }
    CurrentWeather currentWeather = weatherAPI.getCurrentWeather(location);
    String postgresJdbcUrl = getPostgresJdbcUrl();
    if (!postgresJdbcUrl.isEmpty()) {
        MeasurementRepository measurementRepository = measurementRepositorySupplier.apply(postgresJdbcUrl);
        measurementRepository.saveMeasurement(new TemperatureMeasurement(ZonedDateTime.now(), currentWeather.getTemperatureInCelsius()));
    }
    return Optional.of(currentWeather.getTemperatureInCelsius());
}

private String getPostgresJdbcUrl() {
    String postgresJdbcUrl = System.getProperty("POSTGRES_JDBC_URL", System.getenv("POSTGRES_JDBC_URL"));
    return Optional.ofNullable(postgresJdbcUrl).orElse("");
}
```

On to the last wire up, which is in the console app

```xml
Weather weather = new Weather(new WttrClient("http://wttr.in"), () -> new MeasurementRepository());
```

now we uncover the interesting integration issue, we cannot instantiate measurement repository here! so lets refactor our
wire up implementation and simplify it and also if we pass in the string and perform conditional object construction
then we cannot mock it! So what is the answer?

The core of the problem is this good-looking constructor which by definition has to give an option 

```java
public MeasurementRepository(String jdbcUrl) {
    this.jdbi = Jdbi.create(jdbcUrl);
}
```

what if we change this into a function and make the construction optional?

Let's write a test first

```java
public class MeasurementRepositoryConstructionTest {
    @ParameterizedTest
    @NullAndEmptySource
    public void fromJdbcUrlShouldReturnEmptyOptionalIfUrlValueIsNullOrEmpty(String value) {
        Optional<MeasurementRepository> measurementRepository = MeasurementRepository.fromJdbcUrl(value);

        assertEquals(Optional.empty(), measurementRepository);
    }

    @Test
    public void fromJdbcUrlShouldReturnInstanceIf() {
        Optional<MeasurementRepository> measurementRepository = MeasurementRepository.fromJdbcUrl("value");

        assertTrue(measurementRepository.isPresent());
    }
}
```

```java
public static Optional<MeasurementRepository> fromJdbcUrl(String jdbcUrl) {
    if(jdbcUrl == null || jdbcUrl.isEmpty()) {
        return Optional.empty();
    }
    return Optional.of(new MeasurementRepository(jdbcUrl));
}
```

now REFACTORing the wire up code with this one

```java
@Test
public void currentTemperatureInCelsiusShouldPersistTemperatureInformationWhenPostgresUrlIsSetInEnvironment() {
    WeatherAPI weatherAPI = mock(WeatherAPI.class);
    MeasurementRepository measurementRepository = mock(MeasurementRepository.class);
    Weather weather = new Weather(weatherAPI, Optional.of(measurementRepository));
    when(weatherAPI.getCurrentWeather("chennai")).thenReturn(new CurrentWeather(30));

    weather.currentTemperatureInCelsius("chennai");

    verify(measurementRepository).saveMeasurement(any(TemperatureMeasurement.class));
}
```

```java
public class Weather {

    private final WeatherAPI weatherAPI;
    private final Optional<MeasurementRepository> measurementRepository;

    public Weather(WeatherAPI weatherAPI, Optional<MeasurementRepository> measurementRepository) {
        this.weatherAPI = weatherAPI;
        this.measurementRepository = measurementRepository;
    }

    public Optional<Integer> currentTemperatureInCelsius(String location) {
        if (location == null || location.equals("")) {
            return Optional.empty();
        }
        CurrentWeather currentWeather = weatherAPI.getCurrentWeather(location);
        this.measurementRepository.ifPresent(repo -> {
            repo.saveMeasurement(new TemperatureMeasurement(ZonedDateTime.now(), currentWeather.getTemperatureInCelsius()));
        });
        return Optional.of(currentWeather.getTemperatureInCelsius());
    }

}
```

And the main wireup

```java
public class App {
    public static void main(String[] args) {
        if(args.length == 0){
            return;
        }
        Weather weather = new Weather(new WttrClient("http://wttr.in"), MeasurementRepository.fromJdbcUrl(getPostgresJdbcUrl()));
        Optional<Integer> temperature = weather.currentTemperatureInCelsius(args[0]);
        temperature.ifPresent(System.out::println);
    }

    private static String getPostgresJdbcUrl() {
        String postgresJdbcUrl = System.getProperty("POSTGRES_JDBC_URL", System.getenv("POSTGRES_JDBC_URL"));
        return Optional.ofNullable(postgresJdbcUrl).orElse("");
    }
}
```

If you notice apart from the app being working it is also "self-testing" hence we can refactor whatever we want
with great confidence that it won't break the system! That's the main crux!

That's all folks :-)