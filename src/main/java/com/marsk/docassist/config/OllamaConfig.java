package com.marsk.docassist.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Ollama integration
 */
@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.model-name:llama3.2}")
    private String ollamaModelName;

    public String getOllamaBaseUrl() {
        return ollamaBaseUrl;
    }

    public String getOllamaModelName() {
        return ollamaModelName;
    }
}
