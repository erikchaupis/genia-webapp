package com.genia.app.client.config;

import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.vertexai.embedding.VertexAiEmbeddingUtils;
import org.springframework.ai.vertexai.embedding.text.VertexAiTextEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.context.annotation.Primary;

@Configuration
public class EmbeddingModelConfig {

    @Bean
    @Primary
    public EmbeddingModel embeddingModel(VertexAiTextEmbeddingModel vertexAiTextEmbeddingModel, OpenAiEmbeddingModel openAiEmbeddingModel) {
        return vertexAiTextEmbeddingModel;
    }
}