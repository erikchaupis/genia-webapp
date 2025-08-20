package com.genia.app.client.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GeminiResponse {

    private List<Candidate> candidates;

    @JsonProperty("usageMetadata")
    private UsageMetadata usageMetadata;

    private String modelVersion;
    private String responseId;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Candidate {
        private Content content;
        private String finishReason;
        private double avgLogprobs;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Content {
        private List<Part> parts;
        private String role;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Part {
        @JsonProperty("functionCall")
        private FunctionCall functionCall;
        private String text;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FunctionCall {
        private String name;
        private Map<String, Object> args;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsageMetadata {
        private int promptTokenCount;
        private int candidatesTokenCount;
        private int totalTokenCount;
        private List<TokenDetail> promptTokensDetails;
        private List<TokenDetail> candidatesTokensDetails;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class TokenDetail {
        private String modality;
        private int tokenCount;
    }
}
