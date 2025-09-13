package com.study.controller;

import com.server.SSEServer;
import com.study.enums.SSEEnum;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
@RestController
@RequestMapping("/sse")
public class SSEController {
    @GetMapping(path = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connet(@RequestParam String userId) {
        return SSEServer.connet(userId);
    }
    @GetMapping(path = "/send", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Object sendMessage(@RequestParam String userId, @RequestParam String message) {
        try {
            SSEServer.sendMessage(userId, message, SSEEnum.MESSAGE);
            return "OK";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }

    }
}



