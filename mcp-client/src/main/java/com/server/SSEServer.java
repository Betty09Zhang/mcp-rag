package com.server;

import com.study.enums.SSEEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@Slf4j
public class SSEServer {
    private static final Map<String, SseEmitter> sseClient= new ConcurrentHashMap<String, SseEmitter>();
    public static SseEmitter connet(String userId) {
        // 清理已存在的连接
        SseEmitter existingEmitter = sseClient.get(userId);
        if (existingEmitter != null) {
            try {
                existingEmitter.complete();
            } catch (Exception e) {
                log.warn("关闭旧连接时发生异常，userId: {}", userId, e);
            }
        }

        // 设置30分钟超时时间
        SseEmitter sseEmitter = new SseEmitter(30 * 60 * 1000L);

        sseEmitter.onTimeout(() -> {
            log.info("用户{} SSE连接超时", userId);
            timeoutCallback(userId).run();
            // 可以发送特定事件通知客户端进行重连
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("timeout")
                        .data("Connection timeout, please reconnect"));
            } catch (IOException e) {
                log.error("发送超时通知失败，userId: {}", userId, e);
            }
        });

        sseEmitter.onCompletion(completeCallback(userId));
        sseEmitter.onError(errorCallback(userId));

        sseClient.put(userId, sseEmitter);
        log.info("用户{}建立SSE连接成功", userId);
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


    public static void sendSSEMessage(SseEmitter sseEmitter, String userId, String message, SSEEnum sseEnum) throws IOException {
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(userId)
                    .data(message)
                    .name(sseEnum.value));
        } catch (Exception e) {
            log.error("sse发送异常");
            remove(userId);
        }

    }

    public static void sendMessage(String userId, String message, SSEEnum sseEnum) throws IOException {
        if (CollectionUtils.isEmpty(sseClient)) {
            return;
        } else if (sseClient.containsKey(userId)) {
            SseEmitter emitter = sseClient.get(userId);
            try {
                sendSSEMessage(emitter, userId, message, sseEnum);
            } catch (IllegalStateException e) {
                // emitter已经完成，需要重新建立连接
                log.warn("用户{}的连接已关闭，需要客户端重新连接", userId);
                remove(userId);
                // 这里可以考虑通知客户端重新连接，或者根据业务需求处理
            }
        }
    }
}
