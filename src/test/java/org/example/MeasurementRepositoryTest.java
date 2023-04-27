package org.example;

import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
