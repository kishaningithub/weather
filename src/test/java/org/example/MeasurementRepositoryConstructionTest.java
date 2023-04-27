package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
