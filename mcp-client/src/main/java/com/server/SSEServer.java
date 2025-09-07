package com.server;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {
    private static final Map<String, SseEmitter> sseClient= new ConcurrentHashMap<String, SseEmitter>();
    public static SseEmitter connet(String userId) {
        SseEmitter sseEmitter = new SseEmitter();
        sseEmitter.onTimeout(timeoutCallback(userId));
        sseEmitter.onCompletion(completeCallback(userId));
        sseEmitter.onError(errorCallback(userId));
        sseClient.put(userId, sseEmitter);
        return sseEmitter;
    }

    public static Runnable send(String userId, String message) {
        return () -> {
            SseEmitter sseEmitter = sseClient.get(userId);
            if (sseEmitter != null) {
                try {
                    sseEmitter.send(message);
                } catch (Exception e) {
                    sseClient.remove(userId);
                }
            }
        };
    }

    public static Consumer<Throwable> errorCallback(String userId) {
        return Throwable -> {
            log.error("sse发生异常");
            remove(userId);
        };

    }

    public static Runnable timeoutCallback(String userId) {
        return () -> {
            remove(userId);
            log.info("用户{}已断开连接", userId);

        };
    }
    public static Runnable completeCallback(String userId) {
        return () -> {
            remove(userId);
            log.info("用户{}连接已完成", userId);

        };
    }
    private static void remove(String userId) {
        sseClient.remove(userId);
    }
}
