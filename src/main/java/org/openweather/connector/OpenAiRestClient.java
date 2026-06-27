package org.openweather.connector;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.annotation.RegisterClientHeaders;
import org.openweather.domain.ChatCompletionRequest;
import org.openweather.domain.ChatCompletionResponse;

@RegisterRestClient(configKey = "openai")
@RegisterClientHeaders(OpenAiAuthHeadersFactory.class) // chèn Authorization: Bearer ...
@Path("/v1")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface OpenAiRestClient {
    @POST @Path("/chat/completions")
    ChatCompletionResponse chatCompletions(ChatCompletionRequest request);
}

