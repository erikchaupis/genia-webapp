package com.genia.app.client.config;

import com.genia.app.client.gemini.GeminiFlashModel;
import com.genia.app.tool.CommonTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.ai.vertexai.gemini.VertexAiGeminiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class ChatClientConfig {

    @Bean("gemini")
    public ChatClient gemini(GeminiFlashModel geminiFlashModel, CommonTool commonTool) {
        geminiFlashModel.setCallbacks(getTools(commonTool));
        return ChatClient.builder(geminiFlashModel)
                .defaultAdvisors(getInMemoryChatAdvisor())
                .build();
    }

    @Bean("vertex")
    public ChatClient vertex(VertexAiGeminiChatModel chatModel, CommonTool commonTool) {
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(getTools(commonTool))
                .defaultAdvisors(getInMemoryChatAdvisor())
                .build();
    }

    @Bean("openai")
    public ChatClient openai(OpenAiChatModel chatModel, CommonTool commonTool) {
        return ChatClient.builder(chatModel)
                .defaultToolCallbacks(getTools(commonTool))
                .defaultAdvisors(getInMemoryChatAdvisor())
                .build();
    }

    private List<ToolCallback> getTools(CommonTool commonTool) {
        MethodToolCallbackProvider provider = MethodToolCallbackProvider.builder().toolObjects(commonTool).build();
        return List.of(provider.getToolCallbacks());
    }

    private MessageChatMemoryAdvisor getInMemoryChatAdvisor() {
        ChatMemoryRepository repository = new InMemoryChatMemoryRepository();
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }
}
