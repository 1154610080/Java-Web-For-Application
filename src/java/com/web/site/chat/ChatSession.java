package com.web.site.chat;

import javax.websocket.Session;
import java.util.function.Consumer;

/**
 * 聊天Session类
 *
 *   聊天服务器终端将会使用本类将请求聊天的用户关联
 * 响应请求的客户支持代表，它包含了消息的打开和聊天中众多消息的发送
 *
 * @author Egan
 * @date 2018/9/2 16:01
 **/
public class ChatSession {

    private long sessionId;
    private String customerUsername;
    private Session customer;
    private String representativeUsername;
    private Session representative;
    private Consumer<Session> onRepresentativeJoin;
    private ChatMessage creationMessage;

    public long getSessionId() {
        return sessionId;
    }

    public void setSessionId(long sessionId) {
        this.sessionId = sessionId;
    }

    public String getCustomerUsername() {
        return customerUsername;
    }

    public void setCustomerUsername(String customerUsername) {
        this.customerUsername = customerUsername;
    }

    public Session getCustomer() {
        return customer;
    }

    public void setCustomer(Session customer) {
        this.customer = customer;
    }

    public String getRepresentativeUsername() {
        return representativeUsername;
    }

    public void setRepresentativeUsername(String representativeUsername) {
        this.representativeUsername = representativeUsername;
    }

    public Session getRepresentative() {
        return representative;
    }

    public void setOnRepresentativeJoin(Consumer<Session> onRepresentativeJoin) {
        this.onRepresentativeJoin = onRepresentativeJoin;
    }

    public void setOnRepresentativeJoin(Session representative){
        this.representative = representative;
        if(this.onRepresentativeJoin != null)
            this.onRepresentativeJoin.accept(representative);
    }

    public void setRepresentative(Session representative) {
        this.representative = representative;
    }

    public ChatMessage getCreationMessage() {
        return creationMessage;
    }

    public void setCreationMessage(ChatMessage creationMessage) {
        this.creationMessage = creationMessage;
    }


}
