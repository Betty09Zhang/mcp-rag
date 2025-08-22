package com.study.controller;

import com.study.service.ModelService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.retry.NonTransientAiException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/hello")
public class TestController {

    @Resource
    private ModelService modelService;

    @GetMapping("/test")
    public String test(){
        return "test";
    }

    @GetMapping("/search")
    public String search(String keyword){
        try {
            if (keyword == null || keyword.trim().isEmpty()) {
                return "Please provide a keyword parameter";
            }
            return modelService.askModel(keyword);
        } catch (NonTransientAiException e) {
            return "AI Service Error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected Error: " + e.getMessage();
        }
    }
    @GetMapping("/searchByStream")
    public Flux<ChatResponse> searchByStream(String keyword){
        try {

            return modelService.askModelStream(keyword);
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            return null;
        }

    }
    @GetMapping("/searchByStr")
    public Flux<String> searchByStr(String keyword, HttpServletResponse response){
        try {
            response.setCharacterEncoding("UTF-8");
            return modelService.askModelStreamStr(keyword);
        } catch (Exception e) {
            System.out.println("Unexpected Error: " + e.getMessage());
            return null;
        }

    }
}

