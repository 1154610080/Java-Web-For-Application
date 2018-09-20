package com.web.site.chat;

import java.util.List;

/**
 * 聊天服务
 *  负责处理会话和日志的业务逻辑，帮助ChatEndPoint更专注于处理Websocket
 *
 * @author Egan
 * @date 2018/9/17 20:27
 **/
public interface ChatService {

    CreateResult createSession(String user);
    JoinResult joinSession(long id, String user);
    ChatMessage leaveSession(ChatSession session, String user, ReasonForLeaving reason);
    void logMessage(ChatSession session, ChatMessage message);
    List<ChatSession> getPendingSessions();

    public static enum ReasonForLeaving{
        Normal,
        LOGGED_OUT,
        ERROR
    }

}
