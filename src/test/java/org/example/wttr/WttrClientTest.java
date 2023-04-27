package org.example.wttr;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.example.CurrentWeather;
import org.example.wttr.WttrClient;
import org.junit.jupiter.api.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


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
