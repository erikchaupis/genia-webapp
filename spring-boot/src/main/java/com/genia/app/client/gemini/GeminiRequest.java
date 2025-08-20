package com.genia.app.client.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeminiRequest {

    public static Map<String, Object> buildRequest(String message, List<ToolCallback> callbacks) {

        List<Map<String, Object>> functions = convertToolCallbacksToFunctions(callbacks);
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("contents", List.of(
                Map.of("parts", List.of(Map.of("text", message)))
        ));

        if (functions != null && !functions.isEmpty()) {
            requestBody.put("tools", List.of(Map.of("functionDeclarations", functions)));
        }
        return requestBody;
    }

    public static List<Map<String, Object>> convertToolCallbacksToFunctions(List<ToolCallback> toolCallbacks) {
        List<Map<String, Object>> functions = new ArrayList<>();
        if (toolCallbacks == null) return functions;
        for (ToolCallback toolCallback : toolCallbacks) {
            ToolDefinition def = toolCallback.getToolDefinition();

            Map<String, Object> func = new HashMap<>();
            func.put("name", def.name());
            func.put("description", def.description());

            Map<String, Object> paramsSchema = getParametersSchemaFromDefinition(def);
            paramsSchema.remove("$schema");
            paramsSchema.remove("additionalProperties");
            func.put("parameters", paramsSchema);
            functions.add(func);
        }
        return functions;
    }

    private static Map<String, Object> getParametersSchemaFromDefinition(ToolDefinition def) {
        try {
            String inputSchemaJson = def.inputSchema();
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(inputSchemaJson, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new HashMap<>();
    }

    public static Map<String, Object> buildToolResultRequest(String toolResult, GeminiResponse.Part part) {
        String functionName = part.getFunctionCall().getName();
        String result = toolResult;

        Map<String, Object> body = Map.of(
                "contents", List.of(
                        Map.of(
                                "role", "model",
                                "parts", List.of(
                                        Map.of(
                                                "functionCall", Map.of(
                                                        "name", functionName,
                                                        "args", Map.of()
                                                )
                                        )
                                )
                        ),
                        Map.of(
                                "role", "user",
                                "parts", List.of(
                                        Map.of(
                                                "functionResponse", Map.of(
                                                        "name", functionName,
                                                        "response", Map.of("result", result)
                                                )
                                        )
                                )
                        )
                ),
                "tools", List.of(
                        Map.of(
                                "functionDeclarations", List.of(
                                        Map.of(
                                                "name", functionName,
                                                "parameters", Map.of(
                                                        "type", "object",
                                                        "properties", Map.of()
                                                )
                                        )
                                )
                        )
                )
        );
        return body;
    }
}
