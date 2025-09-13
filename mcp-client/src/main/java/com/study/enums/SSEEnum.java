package com.study.enums;

public enum SSEEnum {
    MESSAGE("message", "单词发送的普通类型和消息"),
    ADDED("added", "单词发送的添加类型"),
    FINISHED("finished", "消息完成"),
    CUSTOM_EVENT("event", "单词发送的普通类型消息"),
    DONE("done", "单词发送的完成类型");
    public final String value;
    public final String desc;
    SSEEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
