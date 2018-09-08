package com.web.site.chat;

import java.time.OffsetDateTime;

/**
 * 聊天消息POJO
 *
 * @author Egan
 * @date 2018/9/2 15:45
 **/
public class ChatMessage {
    private OffsetDateTime timeStamp;
    private Type type;
    private String user;
    private String content;

    public OffsetDateTime getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(OffsetDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public static enum Type{
        STARTED, JOINED, ERROR, LEFT, TEXT
    }

}
