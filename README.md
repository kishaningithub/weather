# Weather App

CLI app that can print the current temperature and if you are interested in analytics of this data it can optionally
dump this data in a postgres instance of your choice

## Usage

### Run from source

- Checkout this repo and run the following commands
```bash
$ mvn clean package

# Getting current temperature of a city
$ java -jar target/*-shaded.jar chennai
34

# Persisting in postgres db of your choice
# Create table in your postgres compatible db using command - create table temperature_measurements(measurement_time timestamptz, temperature_in_celsius integer);
$ export POSTGRES_JDBC_URL="<<full jdbc postgres url>>"
$ java -jar target/*-shaded.jar chennai
```


## Development process

This app was built fully following the TDD process, step by step
explanation on how this app was developed can be found [here](DEVELOPMENT_PROCESS.md)