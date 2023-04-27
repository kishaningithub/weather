package org.example;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AppTest {
    @Test
    public void testCliInterfaceShouldNotFailIfNoInputIsGiven() {
        assertDoesNotThrow(() -> App.main(new String[]{}));
    }

    @Test
    public void testCliInterfaceShouldGiveTheTemperatureForTheGivenLocation() {
        ByteArrayOutputStream result = new ByteArrayOutputStream();
        System.setOut(new PrintStream(result));

        App.main(new String[]{"chennai"});

        int actualTemperature = Integer.parseInt(result.toString().trim());
        assertTrue(actualTemperature >= 20 && actualTemperature <= 55 );
    }
}
