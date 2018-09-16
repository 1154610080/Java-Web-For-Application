package com.web.site;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Session注册实现类
 *
 * @author Egan
 * @date 2018/9/16 21:30
 **/
public class DefaultSessionRegistry implements SessionRegistry{

    private final Map<String, HttpSession> sessions = new Hashtable<>();

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
    }

    @Override
    public int getNumberOfSessions() {
        return sessions.size();
    }

    @Override
    public List<HttpSession> getAllSessions() {
        return new ArrayList<>(sessions.values());
    }
}
