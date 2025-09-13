package com.study.service;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import reactor.core.publisher.Flux;

public interface
ModelService {
    public String askModel(String prompt);
    public Flux<ChatResponse> askModelStream(String prompt);
    public Flux<String> askModelStreamStr(String prompt);
}
