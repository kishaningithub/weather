package org.example;

import org.jdbi.v3.core.Jdbi;

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
