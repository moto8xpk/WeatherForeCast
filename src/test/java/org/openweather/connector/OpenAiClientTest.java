package org.openweather.connector;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openweather.domain.ChatCompletionResponse;
import org.openweather.domain.ChatCompletionResponse.Choice;
import org.openweather.domain.Message;
import org.openweather.domain.WeatherContext;
import org.openweather.domain.WeatherSummary;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class OpenAiClientTest {

    private OpenAiClient client;
    private OpenAiRestClient rest;

    private final WeatherContext ctx = new WeatherContext(
            "src", "HCM", 10.0, 106.0, Instant.ofEpochSecond(1_700_000_000L),
            30.0, 33.0, 70.0, Double.NaN, 4.0, 0.0, "clear", List.of());

    @BeforeEach
    void setUp() {
        client = new OpenAiClient();
        rest = mock(OpenAiRestClient.class);
        client.client = rest;
        client.model = "gpt-4o-mini";
        client.temperature = 0.2;
        client.maxTokens = 700;
        client.mapper = new ObjectMapper().findAndRegisterModules();
    }

    private static ChatCompletionResponse responseWith(String content) {
        return new ChatCompletionResponse(
                "id", "chat.completion", 1L, "gpt-4o-mini",
                List.of(new Choice(0, new Message("assistant", content), "stop")),
                null);
    }

    @Test
    void summarize_withPlainJson_parsesWeatherSummary() {
        when(rest.chatCompletions(any())).thenReturn(responseWith(
                "{\"headline\":\"Hot\",\"brief\":\"Sunny day\",\"tips\":[\"Drink water\"],\"riskLevel\":\"LOW\"}"));

        WeatherSummary s = client.summarize(ctx, "HCM", "vi");

        assertEquals("Hot", s.headline());
        assertEquals("Sunny day", s.brief());
        assertEquals(List.of("Drink water"), s.tips());
        assertEquals("LOW", s.riskLevel());
    }

    @Test
    void summarize_withMarkdownFencedJson_stripsFences() {
        when(rest.chatCompletions(any())).thenReturn(responseWith(
                "```json\n{\"headline\":\"H\",\"brief\":\"B\",\"tips\":[],\"riskLevel\":\"HIGH\"}\n```"));

        WeatherSummary s = client.summarize(ctx, "HCM", null); // null lang -> "vi" branch

        assertEquals("H", s.headline());
        assertEquals("HIGH", s.riskLevel());
    }

    @Test
    void summarize_withNullContent_returnsEmptySummary() {
        when(rest.chatCompletions(any())).thenReturn(responseWith(null));

        WeatherSummary s = client.summarize(ctx, "HCM", "en");

        assertNull(s.headline());
        assertNull(s.riskLevel());
    }

    @Test
    void summarize_withBlankContent_returnsEmptySummary() {
        when(rest.chatCompletions(any())).thenReturn(responseWith("   "));

        WeatherSummary s = client.summarize(ctx, "HCM", "vi");

        assertNull(s.headline());
    }

    @Test
    void summarize_withFenceButNoNewline_throws() {
        // startsWith("```") true, but no newline -> stripped to "" -> parse fails -> wrapped
        when(rest.chatCompletions(any())).thenReturn(responseWith("```{}"));
        assertThrows(RuntimeException.class, () -> client.summarize(ctx, "HCM", "vi"));
    }

    @Test
    void summarize_withOpeningFenceButNoClosingFence_stillParses() {
        when(rest.chatCompletions(any())).thenReturn(responseWith(
                "```json\n{\"headline\":\"X\",\"brief\":\"b\",\"tips\":[],\"riskLevel\":\"LOW\"}"));

        WeatherSummary s = client.summarize(ctx, "HCM", "vi");

        assertEquals("X", s.headline());
    }

    @Test
    void summarize_whenResponseNull_throws() {
        when(rest.chatCompletions(any())).thenReturn(null);
        assertThrows(RuntimeException.class, () -> client.summarize(ctx, "HCM", "vi"));
    }

    @Test
    void summarize_whenChoicesNull_throws() {
        when(rest.chatCompletions(any())).thenReturn(
                new ChatCompletionResponse("id", "o", 1L, "m", null, null));
        assertThrows(RuntimeException.class, () -> client.summarize(ctx, "HCM", "vi"));
    }

    @Test
    void summarize_whenChoicesEmpty_throws() {
        when(rest.chatCompletions(any())).thenReturn(
                new ChatCompletionResponse("id", "o", 1L, "m", List.of(), null));
        assertThrows(RuntimeException.class, () -> client.summarize(ctx, "HCM", "vi"));
    }
}
