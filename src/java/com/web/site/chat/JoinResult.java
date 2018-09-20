package com.web.site.chat;

/**
 * 加入聊天结果pojo
 *
 * @author Egan
 * @date 2018/9/17 9:33
 **/
public class JoinResult {

    private final ChatSession chatSession;
    private final ChatMessage chatMessage;

    public JoinResult(ChatSession chatSession, ChatMessage chatMessage) {
        this.chatSession = chatSession;
        this.chatMessage = chatMessage;
    }

    public ChatSession getChatSession() {
        return chatSession;
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }
}
