package com.web.site.chat;

import com.web.site.SessionRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.web.socket.server.standard.SpringConfigurator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
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
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.attribute.UserPrincipal;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Consumer;

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

    private static final Logger log = LogManager.getLogger();

    /**
     * 存储浏览器回应ping的pong消息
     **/
    private static final byte[] pongData =
            "This is PONG country.".getBytes(StandardCharsets.UTF_8);

    /**
     * 该方法引用将注册和取消注册@PostContruct中注册的回调方法
     **/
    private final Consumer<HttpSession> callback = this::httpSessionRemoved;

    private boolean closed = false;
    private HttpSession httpSession;
    private Session wsSession;
    private Session otherSession;
    private ChatSession chatSession;
    private Principal principal;
    private ScheduledFuture<?> pingFuture;

    @Inject
    TaskScheduler taskScheduler;
    @Inject
    ChatService chatService;
    @Inject
    SessionRegistry sessionRegistry;

    /**
     *   当新的握手完成时，该方法将被调用，首先检查HttpSession是否被关联到了Session
     * (在modifyHandshake方法中完成)，以及用户是否已登录，如果聊天会话id为0，那么它会
     * 创建新的聊天会话并添加到等待会话列表中。如果ID大于0，客户支持代表将加入到被请求的会话中，
     * 消息也将发送到两个客户端。
     **/
    @OnOpen
    public void onOpen(Session session, @PathParam("sessionId") long sessionId){
        log.entry(sessionId);

        this.httpSession = EndpointConfigurator.getExposeSession(session);
        this.principal = EndpointConfigurator.getExposePrincipal(session);

        try{
            if(principal == null){
                log.warn("Unauthorized attempt to access chat server.");
                session.close(new CloseReason
                        (CloseReason.CloseCodes.VIOLATED_POLICY, "User not logged in."));
            }
            if(sessionId < 1){
                CreateResult result = this.chatService.createSession(this.principal.getName());
                this.chatSession =  result.getChatSession();
                this.chatSession.setCustomer(session);
                this.chatSession.setOnRepresentativeJoin(
                        s -> this.wsSession = s
                );
                session.getBasicRemote().sendObject(result.getChatMessage());
            }else {
                JoinResult result = this.chatService.joinSession(sessionId, this.principal.getName());
                if(result == null){
                    log.warn("Attempt to join a non-existing chat session {}", sessionId);
                    session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION,
                            "The chat session do not exist!"));
                    return;
                }
                this.chatSession = result.getChatSession();
                this.chatSession.setOnRepresentativeJoin(session);
                this.otherSession = this.chatSession.getCustomer();
                session.getBasicRemote().sendObject(chatSession.getCreationMessage());
                session.getBasicRemote().sendObject(result.getChatMessage());
                otherSession.getBasicRemote().sendObject(result.getChatMessage());
            }
            this.wsSession = session;
            log.debug("OnMessage completed successfully for chat {}.", sessionId);
        } catch (EncodeException | IOException e) {
            this.onError(e);
        }finally {
            log.exit();
        }
    }

    /**
     * 当该方法从某个客户端收到消息时，它将把消息发送到两个客户端
     **/
    @OnMessage
    public void onMessage(Session session, ChatMessage message){

        if(this.closed){
            log.warn("Chat Message received after connection closed.");
            return;
        }

        log.entry();
        message.setUser(this.principal.getName());
        this.chatService.logMessage(chatSession, message);
        try{
            this.wsSession.getBasicRemote().sendObject(message);
            this.otherSession.getBasicRemote().sendObject(message);
        } catch (EncodeException | IOException e) {
            this.onError(e);
        }
        log.exit();
    }

    /**
     *   当会话被关闭引起错误时或HttpSession被销毁时，一个消息将被发送到另一个用户，
     * 通知他聊天已经结束，并关闭两个连接
     **/
    @OnClose
    public void onClose(Session session, CloseReason reason){
        if(reason.getCloseCode() != CloseReason.CloseCodes.NORMAL_CLOSURE)
            log.warn("Abnormal closure {} for reason {}",
                    reason.getCloseCode(), reason.getReasonPhrase());

        synchronized (this){
            if(this.closed)
                return;
            this.close(ChatService.ReasonForLeaving.Normal, null);
        }

    }

    @OnError
    public void onError(Throwable e) {
        log.warn("Error received in WebSocket session.", e);

        synchronized (this){
            if(closed)
                return;
            this.close(ChatService.ReasonForLeaving.ERROR, e.toString());
        }
    }

    /**
     * 向浏览器发送ping消息
     *
     *    计划方法——每隔25s发送一个ping消息，
     * 浏览器将使用pong回应ping消息，当连接关闭时，
     * 它将取消sendPing的执行。
     *    由于ChatEndpoint不是单例bean，所以
     * 使用@scheduled是无效的，为了解决这个问题，
     * 需要直接使用spring的taskScheduler bean。
     *
     * @date 2018/9/17 9:54
     * @param
     * @return void
     **/
    private void sendPing(){
        if(!this.wsSession.isOpen())
            return;
        log.debug("Sending ping Websocket client.");
        try {
            this.wsSession.getBasicRemote()
                    .sendPing(ByteBuffer.wrap(ChatEndpoint.pongData));
        } catch (IOException e) {
            log.warn("Failed to send ping message to Websocket client.", e);
        }
    }

    /**
     * 接收Pong消息
     *
     * @date 2018/9/17 19:46
     * @param message 接收的pong消息
     * @return void
     **/
    @OnMessage
    public void onPong(PongMessage message){
        ByteBuffer data = message.getApplicationData();
        if (!Arrays.equals(ChatEndpoint.pongData, data.array())) {
            log.warn("Receive pong message with incorrect payload.");
        } else {
            log.info("Receive good pong message");
        }
    }

    @PostConstruct
    public void initialize(){

        /**
         * 注册一个回调方法
         **/
        this.sessionRegistry.registerOnRemoveCallback(this.callback);
        
        /**
         * 每隔25s发一个ping消息
         **/
        this.pingFuture = this.taskScheduler.scheduleWithFixedDelay(
                this::sendPing, new Date(System.currentTimeMillis() + 25_000L), 25_000L
        );
    }

    private void httpSessionRemoved(HttpSession httpSession){
        if(httpSession == this.httpSession){
            synchronized (this){
                if(this.closed)
                    return;
                log.info("Chat Session ended abruptly by {} logging out.", this.principal.getName());
                this.close(ChatService.ReasonForLeaving.LOGGED_OUT, null);
            }
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

    private void close(ChatService.ReasonForLeaving reason, String unexpected){

        this.closed = true;

        if(this.pingFuture.isCancelled()) {
            pingFuture.cancel(true);
        }
        this.sessionRegistry.deregisterOnRemoveCallback(this.callback);
        ChatMessage message = chatService.leaveSession(chatSession, principal.getName(), reason);

        if(message != null){
            CloseReason closeReason = reason == ChatService.ReasonForLeaving.ERROR ?
                    new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, unexpected) :
                    new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "Chat Ended");

            synchronized (this.wsSession){
                if(this.wsSession.isOpen()){
                    try {
                        wsSession.getBasicRemote().sendObject(message);
                        wsSession.close();
                    } catch (IOException | EncodeException e) {
                        log.E;
                    }
                }
            }
        }
    }

    /**
     * 终端配置器类
     *
     * @author Egan
     * @date 2018/9/2 16:35
     **/
    public static class EndpointConfigurator
            extends SpringConfigurator {


        private static final String HTTP_SESSION_PROPERTY = "com.web.ws.HTTP_SESSION";
        private static final String PRINCIPAL_KEY = "com.web.ws.user.principal";

        /**
         *   在握手时，该方法将被调用并暴露出底层的HTTP请求，
         * 从该请求中可以获得HttpSession对象，此时可通过HTTP会话保证用户已登录，
         * 如果用户登录了，还可以关闭webSocket会话。
         **/
        @Override
        public void modifyHandshake(ServerEndpointConfig config, HandshakeRequest request, HandshakeResponse response) {
            log.entry();
            super.modifyHandshake(config, request, response);
            HttpSession httpSession = (HttpSession) request.getHttpSession();
            config.getUserProperties().put(
                    HTTP_SESSION_PROPERTY, request.getHttpSession()
            );
            config.getUserProperties().put(PRINCIPAL_KEY, UserPrincipal.g)
            log.exit();

        }

        public static HttpSession getExposeSession(Session session){
            return (HttpSession) session.getUserProperties().get(HTTP_SESSION_PROPERTY);
        }

        public static Principal getExposePrincipal(Session session){
            return (Principal) session.getUserProperties().get(PRINCIPAL_KEY);
        }
    }
}
