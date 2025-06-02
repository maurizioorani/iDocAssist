package com.marsk.docassist.config;

import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for embedding functionality.
 * Only enabled when embedding features are explicitly requested.
 */
@Configuration
@ConditionalOnProperty(name = "docassist.embedding.enabled", havingValue = "true", matchIfMissing = false)
public class EmbeddingConfig {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.embedding-model:nomic-embed-text}")
    private String embeddingModelName;

    /**
     * Creates an Ollama-based embedding model.
     * Only active when docassist.embedding.enabled=true in application properties.
     */
    @Bean
    public EmbeddingModel embeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(embeddingModelName)
                .build();
    }
}
