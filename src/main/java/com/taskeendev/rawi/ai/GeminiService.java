package com.taskeendev.rawi.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class GeminiService {

    private final ChatClient chatClient;

    public GeminiService(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("""
                        You are a content analyst. Analyze the given article text and respond ONLY with valid JSON.
                        Format: {"summary": "2-3 sentence summary in the same language as the article", "category": "one of: Technology, AI, Finance, Health, Politics, Science, Religion, Culture, Other"}
                        Do not include any explanation or markdown. Return raw JSON only.
                        """)
                .build();
    }

    public AiAnalysis analyze(String articleText) {
        return chatClient.prompt()
                .user(articleText)
                .call()
                .entity(AiAnalysis.class);
    }
}
