package com.web.site.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.websocket.*;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import java.io.File;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * 聊天终端类
 *
 * 本类将接收聊天连接并进行适当的协调
 *
 * @author Egan
 * @date 2018/9/2 16:31
 **/
@ServerEndpoint(value = "/chat/{sessionId}",
        encoders = ChatMessageCodec.class,
        decoders = ChatMessageCodec.class,
        configurator = ChatEndpoint.EndpointConfigurator.class)
@WebListener
public class ChatEndpoint implements HttpSessionListener {

    /**
     * 由于该类的每一个实例都将在启动时作为WEB监听器创建，
     * 每次客户端连接到服务器也会创建新的实例，所有所有字段都要使用静态字段
     **/
    private static final Logger log = LogManager.getLogger();

    private static final String HTTP_SESSION_PROPERTY = "com.web.ws.HTTP_SESSION";
    private static final String WS_SESSION_PROPERTY = "com.web.http,WS_SESSION";
    private static long sessionIdSequence = 1L;
    private static final Object sessionIdSequenceLock = new Object();
    private static final Map<Long, ChatSession> chatSessions = new Hashtable<>();
    private static final Map<Session, ChatSession> sessions = new Hashtable<>();
    private static final Map<Session, HttpSession> httpSessions =
            new Hashtable<>();
    public static final List<ChatSession> pendingSessions = new ArrayList<>();

    /**
     *   当新的握手完成时，该方法将被调用，首先检查HttpSession是否被关联到了Session
     * (在modifyHandshake方法中完成)，以及用户是否已登录，如果聊天会话id为0，那么它会
     * 创建新的聊天会话并添加到等待会话列表中。如果ID大于0，客户支持代表将加入到被请求的会话中，
     * 消息也将发送到两个客户端。
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") long sessionId){
        log.entry(sessionId);
        HttpSession httpSession = (HttpSession) session.getUserProperties()
                .get(ChatEndpoint.HTTP_SESSION_PROPERTY);
        try {
            if(httpSession == null || httpSession.getAttribute("username") == null){
                log.warn("Attempt to access chat server while logged out.");
                session.close(new CloseReason(
                        CloseReason.CloseCodes.VIOLATED_POLICY,
                        "You are not logged in!"
                        ));
                return;
            }

            String username = (String)httpSession.getAttribute("username");
            session.getUserProperties().put("username", username);
            ChatMessage message = new ChatMessage();
            message.setTimeStamp(OffsetDateTime.now());
            message.setUser(username);
            ChatSession chatSession;

            if(sessionId < 1){
                log.debug("User starting chat {} is {}.", sessionId, username);
                message.setType(ChatMessage.Type.STARTED);
                message.setContent(username + "started the chat session.");
                chatSession = new ChatSession();
                synchronized (ChatEndpoint.sessionIdSequenceLock){
                    chatSession.setSessionId(ChatEndpoint.sessionIdSequence++);
                }
                chatSession.setCustomer(session);
                chatSession.setCustomerUsername(username);
                chatSession.setCreationMessage(message);
                ChatEndpoint.pendingSessions.add(chatSession);
                ChatEndpoint.chatSessions.put(chatSession.getSessionId(),
                        chatSession);
            }else{
                log.debug("User joining chat {} is {}.", sessionId, username);
                message.setType(ChatMessage.Type.JOINED);
                message.setContent(username + "joined the chat session.");
                chatSession = ChatEndpoint.chatSessions.get(sessionId);
                chatSession.setRepresentative(session);
                chatSession.setRepresentativeUsername(username);
                ChatEndpoint.pendingSessions.remove(chatSession);
                session.getBasicRemote()
                        .sendObject(chatSession.getCreationMessage());
                session.getBasicRemote().sendObject(message);
            }

            ChatEndpoint.sessions.put(session, chatSession);
            ChatEndpoint.httpSessions.put(session, httpSession);
            this.getSessionsFor(httpSession).add(session);
            chatSession.log(message);
            chatSession.getCustomer().getBasicRemote().sendObject(message);
            log.debug("onMessage completed successfully for chat {}.", sessionId);

        } catch (IOException | EncodeException e) {
            e.printStackTrace();
        }finally {
            log.exit();
        }
    }

    /**
     * 当该方法从某个客户端收到消息时，它将把消息发送到两个客户端
     **/
    @OnMessage
    public void onMessage(Session session, ChatMessage message){
        log.entry();
        ChatSession c = ChatEndpoint.sessions.get(session);
        Session other = this.getOtherSession(c, session);
        if(c != null && other != null){
            c.log(message);
            try {
                session.getBasicRemote().sendObject(message);
                other.getBasicRemote().sendObject(message);
            } catch (EncodeException | IOException e) {
                e.printStackTrace();
            }
        }else
            log.warn("Chat message received with only one chat member.");
        log.exit();
    }

    /**
     *   当会话被关闭引起错误时或HttpSession被销毁时，一个消息将被发送到另一个用户，
     * 通知他聊天已经结束，并关闭两个连接
     **/
    @OnClose
    public void onClose(Session session, CloseReason reason){
        if(reason.getCloseCode() == CloseReason.CloseCodes.NORMAL_CLOSURE){
            ChatMessage message = new ChatMessage();
            message.setUser((String)session.getUserProperties().get("username"));
            message.setType(ChatMessage.Type.LEFT);
            message.setTimeStamp(OffsetDateTime.now());
            message.setContent(message.getUser() + "left the chat.");
            try {
                Session other = this.close(session, message);
                if(other != null)
                    other.close();
            } catch (IOException e) {
                log.warn("Problem closing companion chat session.", e);
            }
        }else
            log.warn("Abnormal closure {} for reason [{}].", reason.getCloseCode(),
                    reason.getReasonPhrase());
    }

    @OnError
    public void onError(Session session, Throwable e) {
        log.warn("Error received in WebSocket session.", e);
        ChatMessage message = new ChatMessage();
        message.setUser((String)session.getUserProperties().get("username"));
        message.setType((ChatMessage.Type.ERROR));
        message.setTimeStamp(OffsetDateTime.now());
        message.setContent(message.getUser() + "left the chat due to an error.");
        try{
            Session other = this.close(session, message);
            if(other != null)
                other.close(new CloseReason(
                        CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.toString()
                ));
        } catch (IOException ignore) { }
        finally {
            try {
                session.close(new CloseReason(
                        CloseReason.CloseCodes.UNEXPECTED_CONDITION, e.toString()
                ));
            } catch (IOException ignore) { }
            log.exit();
        }
    }


    @Override
    public void sessionCreated(HttpSessionEvent se) {
        /**
         * do nothing
         **/
    }

    /**
     *   当会话无效时，该方法将被调用，
     * 并且终端也会终止该聊天会话。
     **/
    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession httpSession = event.getSession();
        log.entry(httpSession.getId());
        if(httpSession.getAttribute(WS_SESSION_PROPERTY) != null){
            ChatMessage message = new ChatMessage();
            message.setUser((String)httpSession.getAttribute("username"));
            message.setType(ChatMessage.Type.LEFT);
            message.setTimeStamp(OffsetDateTime.now());
            message.setContent(message.getUser() + "logged out.");
            for(Session session : new ArrayList<>(this.getSessionsFor(httpSession))){
                log.info("Closing chat session {} belonging to HTTP session {}.",
                        session.getId(), httpSession.getId());
                try{
                    session.getBasicRemote().sendObject(message);
                    Session other = this.close(session, message);
                    if(other != null)
                        other.close();
                } catch (EncodeException | IOException e) {
                    log.warn("Problem closing companion chat session.");
                }finally {
                    try {
                        session.close();
                    } catch (IOException ignore) { }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private synchronized ArrayList<Session> getSessionsFor(HttpSession session){
        log.entry();
        try {
            if(session.getAttribute(WS_SESSION_PROPERTY) == null)
                session.setAttribute(WS_SESSION_PROPERTY, new ArrayList<>());
            return (ArrayList<Session>) session.getAttribute(WS_SESSION_PROPERTY);
        }catch (IllegalStateException e){
            return new ArrayList<>();
        }finally {
            log.exit();
        }
    }

    private Session getOtherSession(ChatSession c, Session s){
        log.entry();
        return log.exit(c == null ? null :
                (s == c.getCustomer() ? c.getRepresentative() : c.getCustomer()));
    }

    private Session close(Session s, ChatMessage message){
        log.entry(s);
        ChatSession c = ChatEndpoint.sessions.get(s);
        Session other = this.getOtherSession(c, s);
        ChatEndpoint.sessions.remove(s);
        HttpSession h = ChatEndpoint.httpSessions.get(s);
        if(h != null)
            this.getSessionsFor(h).remove(s);
        if(c != null){
            c.log(message);
            ChatEndpoint.pendingSessions.remove(c);
            ChatEndpoint.chatSessions.remove(c.getSessionId());
            try {
                c.writenChatLog(new File("chat." + c.getSessionId() + ".log"));
            } catch (IOException e) {
                log.error("Could not write chat log due to error");
            }
        }
        if(other != null){
            ChatEndpoint.sessions.remove(other);
            h = ChatEndpoint.httpSessions.get(other);
            if(h != null)
                this.getSessionsFor(h).remove(s);
            try {
                other.getBasicRemote().sendObject(message);
            } catch (EncodeException | IOException e) {
                log.warn("Problem closing companion chat session.", e);
            }
        }
        return log.exit(other);
    }

    /**
     * 终端配置器类
     *
     * @author Egan
     * @date 2018/9/2 16:35
     **/
    public static class EndpointConfigurator
            extends ServerEndpointConfig.Configurator {

        /**
         *   在握手时，该方法将被调用并暴露出底层的HTTP请求，
         * 从该请求中可以获得HttpSession对象，此时可通过HTTP会话保证用户已登录，
         * 如果用户登录了，还可以关闭webSocket会话。
         **/
        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            log.entry();
            super.modifyHandshake(config, request, response);
                config.getUserProperties().put(
                        ChatEndpoint.HTTP_SESSION_PROPERTY, request.getHttpSession()
                );
                log.exit();

        }
    }
}
