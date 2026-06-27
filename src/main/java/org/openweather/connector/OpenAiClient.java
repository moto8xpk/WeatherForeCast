package org.openweather.connector;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.arc.properties.IfBuildProperty;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.openweather.domain.ChatCompletionRequest;
import org.openweather.domain.Message;
import org.openweather.domain.WeatherContext;
import org.openweather.domain.WeatherSummary;
import org.openweather.infra.AiSummarizationException;
import org.openweather.infra.AiSummarizerPort;


import java.util.List;

@ApplicationScoped
@IfBuildProperty(name = "ai.provider", stringValue = "openai") // only 1 active provider
public class OpenAiClient implements AiSummarizerPort {

    @RestClient OpenAiRestClient client;

    @ConfigProperty(name = "ai.openai.model", defaultValue = "gpt-4o-mini")
    String model;
    @ConfigProperty(name = "ai.openai.temperature", defaultValue = "0.2")
    Double temperature;
    @ConfigProperty(name = "ai.openai.maxTokens", defaultValue = "700")
    Integer maxTokens;

    @Inject ObjectMapper mapper;

    @Override
    public WeatherSummary summarize(WeatherContext ctx, String city, String lang) {
        try {
            String ctxJson = mapper.writeValueAsString(ctx);
            var req = new ChatCompletionRequest(
                    model,
                    List.of(
                            new Message("system", "You are a concise weather assistant. Always answer in " + (lang == null ? "vi" : lang) + ". Output strict JSON for WeatherSummary."),
                            new Message("user", """
                  Weather context (JSON):
                  """ + ctxJson + """

                  Task: Return a JSON object matching WeatherSummary {headline, brief, tips[], riskLevel(LOW|MODERATE|HIGH)}.
                  """)
                    ),
                    temperature,
                    maxTokens,
                    ChatCompletionRequest.ResponseFormat.JSON_OBJECT
            );

            var resp = client.chatCompletions(req);
            if (resp == null || resp.choices() == null || resp.choices().isEmpty()) {
                throw new AiSummarizationException("OpenAI returned no choices");
            }
            var content = resp.choices().getFirst().message().content();
            return mapper.readValue(extractJson(content), WeatherSummary.class);
        } catch (JsonProcessingException e) {
            throw new AiSummarizationException("OpenAI request/response (de)serialization failed", e);
        }
    }

    /** Defensive: strip Markdown code fences in case the model wraps JSON despite response_format. */
    private static String extractJson(String content) {
        if (content == null || content.isBlank()) {
            return "{}";
        }
        String s = content.strip();
        if (s.startsWith("```")) {
            int firstNewline = s.indexOf('\n');
            if (firstNewline >= 0) {
                s = s.substring(firstNewline + 1);
            }
            int lastFence = s.lastIndexOf("```");
            if (lastFence >= 0) {
                s = s.substring(0, lastFence);
            }
        }
        return s.strip();
    }
}

