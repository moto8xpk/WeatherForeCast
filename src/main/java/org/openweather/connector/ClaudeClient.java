package org.openweather.connector;

import com.anthropic.client.AnthropicClient;
import com.anthropic.client.okhttp.AnthropicOkHttpClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.openweather.domain.WeatherContext;
import org.openweather.domain.WeatherSummary;
import org.openweather.infra.AiSummarizationException;
import org.openweather.infra.AiSummarizerPort;

import java.util.Optional;

/**
 * Claude (Anthropic) adapter for {@link AiSummarizerPort}.
 *
 * <p>Selected at build time via {@code ai.provider=claude}, mirroring {@link OpenAiClient}
 * ({@code ai.provider=openai}). Only one provider bean is active per build.</p>
 *
 * <p>Uses the official Anthropic Java SDK with structured outputs so the model is constrained
 * to return a valid {@link WeatherSummary} — no manual JSON parsing or fence-stripping needed.</p>
 */
@ApplicationScoped
@IfBuildProperty(name = "ai.provider", stringValue = "claude")
public class ClaudeClient implements AiSummarizerPort {

    @ConfigProperty(name = "ai.anthropic.model", defaultValue = "claude-opus-4-8")
    String model;

    @ConfigProperty(name = "ai.anthropic.maxTokens", defaultValue = "1024")
    Long maxTokens;

    /** Optional explicit key; if absent the SDK falls back to the ANTHROPIC_API_KEY env var. */
    @ConfigProperty(name = "ai.anthropic.api-key")
    Optional<String> apiKey;

    @Inject
    ObjectMapper mapper;

    private AnthropicClient client;

    @PostConstruct
    void init() {
        client = apiKey
                .filter(k -> !k.isBlank())
                .map(k -> AnthropicOkHttpClient.builder().apiKey(k).build())
                .orElseGet(AnthropicOkHttpClient::fromEnv);
    }

    @PreDestroy
    void close() {
        if (client != null) {
            client.close();
        }
    }

    @Override
    public WeatherSummary summarize(WeatherContext ctx, String city, String lang) {
        try {
            String ctxJson = mapper.writeValueAsString(ctx);
            String language = (lang == null || lang.isBlank()) ? "vi" : lang;

            StructuredMessageCreateParams<WeatherSummary> params = MessageCreateParams.builder()
                    .model(model)
                    .maxTokens(maxTokens)
                    .system("You are a concise weather assistant. Always answer in " + language + ".")
                    .outputConfig(WeatherSummary.class)
                    .addUserMessage("""
                            Weather context (JSON):
                            """ + ctxJson + """

                            Task: Summarize today's weather for %s. Produce a WeatherSummary with a short
                            headline, a brief paragraph, a few actionable tips, and an overall riskLevel of
                            LOW, MODERATE, or HIGH.
                            """.formatted(city))
                    .build();

            return client.messages().create(params).content().stream()
                    .flatMap(block -> block.text().stream())
                    .map(typed -> typed.text())
                    .findFirst()
                    .orElseThrow(() -> new AiSummarizationException("Claude returned no structured content"));
        } catch (JsonProcessingException e) {
            throw new AiSummarizationException("Anthropic request serialization failed", e);
        }
    }
}
