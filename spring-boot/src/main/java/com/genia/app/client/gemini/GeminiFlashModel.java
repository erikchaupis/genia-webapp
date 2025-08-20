package com.genia.app.client.gemini;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class GeminiFlashModel implements ChatModel {

    @Value("${google.generative.language.uri}")
    private String uri;

    @Value("${google.generative.language.api-key}")
    private String accessToken;

    private final WebClient webClient;

    @Setter
    private List<ToolCallback> callbacks;

    public GeminiFlashModel() {
        this.webClient = WebClient.create();
    }

    @Override
    public String call(String message) {
        Map<String, Object> requestBody = GeminiRequest.buildRequest(message, callbacks);
        GeminiResponse response = callGemini(requestBody);
        GeminiResponse.Part part = response.getCandidates().get(0).getContent().getParts().get(0);
        if (shouldCallTool(part)) {
            String toolResult = callTool(part);
            Map<String, Object> newRequest = GeminiRequest.buildToolResultRequest(toolResult, part);
            response = callGemini(newRequest);
            return response.getCandidates().get(0).getContent().getParts().get(0).getText();
        }
        return part.getText();
    }

    private boolean shouldCallTool(GeminiResponse.Part part) {
        return part.getFunctionCall() != null && part.getFunctionCall().getName() != null;
    }


    private GeminiResponse callGemini(Map<String, Object> requestBody) {
        log.info("Request to gemini: " + requestBody);
        GeminiResponse response = webClient.post()
                .uri(uri)
                .header("X-goog-api-key", accessToken)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();
        log.info("Response from gemini: " + response);
        return response;
    }

    private String callTool(GeminiResponse.Part part) {
        try {
            String toolName = part.getFunctionCall().getName();
            ToolCallback matchingTool = callbacks.stream()
                    .filter(cb -> cb.getToolDefinition().name().equals(toolName))
                    .findFirst()
                    .orElseThrow();

            ObjectMapper mapper = new ObjectMapper();
            String input = mapper.writeValueAsString(part.getFunctionCall().getArgs());
            String toolResult = matchingTool.call(input);
            log.info("toolResult: " + toolResult);
            return toolResult;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return "Unable to execute the internal tool";
    }


    @Override
    public String call(Message... messages) {
        Prompt prompt = new Prompt(Arrays.asList(messages));
        return call(prompt.getUserMessage().getText());
    }

    @Override
    public ChatResponse call(Prompt prompt) {
        String response = call(prompt.getUserMessage());
        return ChatResponse.builder().generations(List.of(new Generation(new AssistantMessage(response)))).build();
    }

    @Override
    public ChatOptions getDefaultOptions() {
        return ChatModel.super.getDefaultOptions();
    }

    @Override
    public Flux<String> stream(String message) {
        return ChatModel.super.stream(message);
    }

    @Override
    public Flux<String> stream(Message... messages) {
        return ChatModel.super.stream(messages);
    }

    @Override
    public Flux<ChatResponse> stream(Prompt prompt) {
        return ChatModel.super.stream(prompt);
    }
}

