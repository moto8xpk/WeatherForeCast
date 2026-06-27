package org.openweather.resource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.domain.WeatherSummary;
import org.openweather.service.SummarizeTodayUseCase;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.*;

class AiResourceTest {

    private AiResource resource;
    private SummarizeTodayUseCase useCase;
    private final WeatherSummary summary =
            new WeatherSummary("h", "b", List.of("t"), "LOW");

    @BeforeEach
    void setUp() {
        resource = new AiResource();
        useCase = mock(SummarizeTodayUseCase.class);
        resource.summarizeToday = useCase;
    }

    @Test
    void summary_withCity_delegatesToUseCase() {
        when(useCase.handle("Hanoi", "vi")).thenReturn(summary);

        assertSame(summary, resource.summary("Hanoi", "vi"));
        verify(useCase).handle("Hanoi", "vi");
    }

    @Test
    void summary_withBlankCity_passesNull() {
        when(useCase.handle(isNull(), eq("en"))).thenReturn(summary);

        assertSame(summary, resource.summary("", "en"));
        verify(useCase).handle(isNull(), eq("en"));
    }
}
