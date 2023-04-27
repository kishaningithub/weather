package org.example;

import org.jdbi.v3.core.Jdbi;

import java.util.Optional;

public class MeasurementRepository {

    private final Jdbi jdbi;

    public MeasurementRepository(String jdbcUrl, String username, String password) {
        this.jdbi = Jdbi.create(jdbcUrl, username, password);
    }

    public MeasurementRepository(String jdbcUrl) {
        this.jdbi = Jdbi.create(jdbcUrl);
    }

    public static Optional<MeasurementRepository> fromJdbcUrl(String jdbcUrl) {
        if(jdbcUrl == null || jdbcUrl.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new MeasurementRepository(jdbcUrl));
    }

    public void saveMeasurement(TemperatureMeasurement measurement) {
        jdbi.useHandle(handle -> handle
                .createUpdate("insert into temperature_measurements(measurement_time, temperature_in_celsius) values (:measurementTime, :temperatureInCelsius)")
                .bindBean(measurement)
                .execute());
    }
}
