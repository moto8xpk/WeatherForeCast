package org.openweather.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.domain.OpenWeatherMapResponse;
import org.openweather.domain.WeatherSummary;
import org.openweather.infra.AiSummarizerPort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class SummarizeTodayUseCaseTest {

    private SummarizeTodayUseCase useCase;
    private WeatherService weatherService;
    private AiSummarizerPort ai;

    private final WeatherSummary summary =
            new WeatherSummary("h", "b", List.of("t"), "LOW");

    @BeforeEach
    void setUp() {
        weatherService = mock(WeatherService.class);
        ai = mock(AiSummarizerPort.class);
        useCase = new SummarizeTodayUseCase(weatherService, ai);
        when(weatherService.getCurWeather(any())).thenReturn(new OpenWeatherMapResponse());
    }

    @Test
    void handle_withCityAndLang_passesThemThrough() {
        when(ai.summarize(any(), eq("Hanoi"), eq("en"))).thenReturn(summary);

        assertSame(summary, useCase.handle("Hanoi", "en"));

        verify(weatherService).getCurWeather("Hanoi");
        verify(ai).summarize(any(), eq("Hanoi"), eq("en"));
    }

    @Test
    void handle_withNullCityAndLang_appliesDefaults() {
        when(ai.summarize(any(), eq("Default"), eq("vi"))).thenReturn(summary);

        assertSame(summary, useCase.handle(null, null));

        verify(weatherService).getCurWeather(null);
        verify(ai).summarize(any(), eq("Default"), eq("vi"));
    }

    @Test
    void handle_withBlankCityAndLang_appliesDefaults() {
        when(ai.summarize(any(), eq("Default"), eq("vi"))).thenReturn(summary);

        assertSame(summary, useCase.handle("  ", "  "));

        verify(ai).summarize(any(), eq("Default"), eq("vi"));
    }
}
