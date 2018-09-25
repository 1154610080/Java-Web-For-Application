package com.web.site.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * ChatService的实现类
 *
 * @author Egan
 * @date 2018/9/17 20:25
 **/
@Service
public class DefaultChatService implements ChatService{

    private Logger log = LogManager.getLogger();

    private Map<Long, ChatSession> sessions = new Hashtable<>();
    private Map<Long, ChatSession> pendingSessions = new Hashtable<>();
    private Map<Long, List<ChatMessage>> chatLogs = new Hashtable<>();
    private volatile long SESSION_ID_SEQUENCE = 1L;

    @Inject
    ObjectMapper objectMapper;

    /**
     * 创建聊天会话
     *
     * @date 2018/9/17 20:39
     * @param user   用户名
     * @return com.web.site.chat.CreateResult
     **/
    @Override
    public CreateResult createSession(String user) {

        ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setTimestamp(Instant.now());
        message.setType(ChatMessage.Type.STARTED);
        message.setContentCode("message.chat.started.session");
        message.setContentArguments(user);

        ChatSession session = new ChatSession();
        session.setSessionId(getNextSessionId());
        session.setCustomerUsername(user);
        session.setCreationMessage(message);

        sessions.put(session.getSessionId(), session);
        pendingSessions.put(session.getSessionId(), session);
        chatLogs.put(session.getSessionId(), new ArrayList<>());
        this.logMessage(session, message);

        return new CreateResult(session, message);
    }

    /**
     * 加入聊天会话
     *
     * @date 2018/9/17 20:57
     * @param id 请求加入的会话id
	 * @param user  请求加入的用户名
     * @return com.web.site.chat.JoinResult 加入结果
     **/
    @Override
    public JoinResult joinSession(long id, String user) {

        ChatSession session = pendingSessions.remove(id);
        if(session == null){
            log.warn("Attempt join to non-existing session {}.", id);
            return null;
        }
        session.setRepresentativeUsername(user);

        ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setType(ChatMessage.Type.JOINED);
        message.setTimestamp(Instant.now());
        message.setContentCode("message.chat.joined.session");
        message.setContentArguments(user);

        logMessage(session, message);

        return new JoinResult(session, message);
    }

    /**
     * 离开聊天会话
     *
     * @date 2018/9/17 20:58
     * @param session 离开的聊天会话
	 * @param user 离开的用户名
	 * @param reason 离开原因
     * @return com.web.site.chat.ChatMessage
     **/
    @Override
    public ChatMessage leaveSession(ChatSession session, String user, ReasonForLeaving reason) {
        long id = session.getSessionId();
        this.pendingSessions.remove(id); // in case closed before support joined
        if(this.sessions.remove(id) == null)
            return null;

        ChatMessage message = new ChatMessage();
        message.setUser(user);
        message.setTimestamp(Instant.now());
        if(reason == ReasonForLeaving.ERROR)
            message.setType(ChatMessage.Type.ERROR);
        message.setType(ChatMessage.Type.LEFT);
        if(reason == ReasonForLeaving.ERROR)
            message.setContentCode("message.chat.left.chat.error");
        else if(reason == ReasonForLeaving.LOGGED_OUT)
            message.setContentCode("message.chat.logged.out");
        else
            message.setContentCode("message.chat.left.chat.normal");
        message.setContentArguments(user);
        this.logMessage(session, message);

        List<ChatMessage> chatLog = this.chatLogs.remove(id);
        try(FileOutputStream stream = new FileOutputStream("../chat-" + id + ".log"))
        {
            this.objectMapper.writeValue(stream, chatLog);
        }
        catch(IOException e)
        {
            log.error("Error while saving chat log to disk for session {}.", id);
        }

        return message;
    }

    /**
     * 日志消息
     *    向聊天日志中添加消息
     *
     * @date 2018/9/17 20:49
     * @param session 
	 * @param message   
     * @return void 
     **/  
    @Override
    public void logMessage(ChatSession session, ChatMessage message) {
        
        List<ChatMessage> chatLog = chatLogs.get(session.getSessionId());
        if(chatLog == null)
            log.warn("Attempt made to record chat message in non-existing log");
        else
            chatLog.add(message);
    }

    /**
     * 添加Mix-in注解
     **/
    @PostConstruct
    public void initialize(){
        this.objectMapper.addMixInAnnotations(ChatMessage.class,
                ChatMessage.MixInForLogWrite.class);
    }

    @Override
    public List<ChatSession> getPendingSessions() {
        return null;
    }

    private synchronized Long getNextSessionId(){return SESSION_ID_SEQUENCE++;}
}
