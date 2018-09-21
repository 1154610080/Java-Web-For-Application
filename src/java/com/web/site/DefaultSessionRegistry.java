package com.web.site;

import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.util.*;
import java.util.function.Consumer;

/**
 * Session注册实现类
 *
 * @author Egan
 * @date 2018/9/16 21:30
 **/
@Service
public class DefaultSessionRegistry implements SessionRegistry{

    private final Map<String, HttpSession> sessions = new Hashtable<>();
    private final Set<Consumer<HttpSession>> callbacks = new HashSet<>();
    private final Set<Consumer<HttpSession>> callbacksAddedWhileLocked =
            new HashSet<>();

    @Override
    public void addSession(HttpSession session) {
        sessions.put(session.getId(), session);
    }

    @Override
    public void updateSession(HttpSession session, String oldId) {
        synchronized (this.sessions){
            this.sessions.remove(oldId);
            addSession(session);
        }
    }

    @Override
    public void removeSession(HttpSession session) {

        this.sessions.remove(session.getId());
        synchronized (this.callbacks){
            callbacksAddedWhileLocked.clear();
            callbacks.forEach(c -> c.accept(session));
            try {
                this.callbacksAddedWhileLocked.forEach(c->c.accept(session));
            }catch (ConcurrentModificationException ignore){}
        }
    }

    @Override
    public int getNumberOfSessions() {
        return sessions.size();
    }

    @Override
    public List<HttpSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }

    @Override
    public void registerOnRemoveCallback(Consumer<HttpSession> callback) {
        this.callbacksAddedWhileLocked.add(callback);
        synchronized (this.callbacks){
            callbacks.add(callback);
        }
    }

    @Override
    public void deregisterOnRemoveCallback(Consumer<HttpSession> callback) {
        synchronized (this.callbacks){
            this.callbacks.remove(callback);
        }
    }
}
