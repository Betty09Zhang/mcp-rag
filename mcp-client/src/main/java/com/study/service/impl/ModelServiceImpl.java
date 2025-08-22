package com.study.service.impl;

import com.study.service.ModelService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.DefaultChatClientBuilder;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ModelServiceImpl implements ModelService {

    private final ChatClient chatClient;

    public ModelServiceImpl(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.defaultSystem("假设你是个助手，你的名字叫曼曼").build();
    }

    public String askModel(String userInput) {
        // 添加输入验证
        if (userInput == null || userInput.trim().isEmpty()) {
            userInput = "Hello, what can you help me with?";
        }

        try {
            return this.chatClient.prompt(userInput)
                    .call()
                    .content();
        } catch (NonTransientAiException e) {
            // 记录详细错误信息
            throw new RuntimeException("AI service configuration error: " + e.getMessage(), e);
        }
    }

    public Flux<ChatResponse> askModelStream(String userInput) {
        // 添加输入验证
        if (userInput == null || userInput.trim().isEmpty()) {
            userInput = "Hello, what can you help me with?";
        }

        try {
            return this.chatClient.prompt(userInput)
                    .stream()
                    .chatResponse();
        } catch (NonTransientAiException e) {
            // 记录详细错误信息
            throw new RuntimeException("AI service configurationerror: " + e.getMessage(), e);
        }
    }

    public Flux<String> askModelStreamStr(String userInput) {
        // 添加输入验证
        if (userInput == null || userInput.trim().isEmpty()) {
            userInput = "Hello, what can you help me with?";
        }

        try {
            return this.chatClient.prompt(userInput)
                    .stream()
                    .content();
        } catch (NonTransientAiException e) {
            // 记录详细错误信息
            throw new RuntimeException("AI service configuration error: " + e.getMessage(), e);
        }
    }
}

