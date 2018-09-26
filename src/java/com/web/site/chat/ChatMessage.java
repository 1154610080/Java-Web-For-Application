package com.web.site.chat;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.Instant;

/**
 * 聊天消息POJO
 * 支持国际化
 *
 * @author Egan
 * @date 2018/9/2 15:45
 **/
public class ChatMessage implements Cloneable {
    private Instant timestamp;
    private Type type;
    private String user;
    private String contentCode;
    private Object[] contentArguments;
    private String localizedContent;
    private String userContent;

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
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

    public String getContentCode() {
        return contentCode;
    }

    public void setContentCode(String contentCode) {
        this.contentCode = contentCode;
    }

    public Object[] getContentArguments() {
        return contentArguments;
    }

    public void setContentArguments(Object ... contentArguments) {
        this.contentArguments = contentArguments;
    }

    public String getLocalizedContent() {
        return localizedContent;
    }

    public void setLocalizedContent(String localizedContent) {
        this.localizedContent = localizedContent;
    }

    public String getUserContent() {
        return userContent;
    }

    public void setUserContent(String userContent) {
        this.userContent = userContent;
    }

    @Override
    @SuppressWarnings("CloneDoesntDeclareCloneNotSupportException")
    protected ChatMessage clone(){
        try {
            return (ChatMessage) super.clone();
        }catch (CloneNotSupportedException e){
            throw new RuntimeException("impossible clone not supported", e);
        }
    }

    public static enum Type{
        STARTED, JOINED, ERROR, LEFT, TEXT
    }

    /**
     * 以下两个方法是支持Jackson Data Processor 的Mix-in注解特性的特殊类，
     **/
    static abstract class MixInForLogWrite{
        /**
         * localizedContent不应被写入日志中，因为它是为某个特定用户进行本地化的。
         **/
        @JsonIgnore public abstract String getLocalizedContent();
        @JsonIgnore public abstract void setLocalizedContent();
    }



    static abstract class MixInForWebSocket{
        /**
         * contentCode和contentArguments不需要Websocket进行传输，
         * 因为消息已经本地化了
         **/
        @JsonIgnore public abstract String getContentCode();
        @JsonIgnore public abstract void setContentCode(String c);
        @JsonIgnore public abstract Object[] getContentArguments();
        @JsonIgnore public abstract void setContentArguments(Object[] c);
    }


}
