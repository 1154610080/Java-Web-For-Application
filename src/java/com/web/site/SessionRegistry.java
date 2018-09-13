package com.web.site;

import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 会话注册器
 * 负责添加和删除会话
 *
 * @author Egan
 * @date 2018/9/13 22:07
 **/
public final class SessionRegistry {

    private static final Map<String, HttpSession> SESSIONS = new Hashtable<>();

    public static void addSession(HttpSession session){
        SESSIONS.put(session.getId(), session);
    }

    public static void updateSession(HttpSession session, String oldId){
        synchronized (SESSIONS){
            SESSIONS.remove(oldId);
            addSession(session);
        }
    }

    public static void removeSession(HttpSession session){
        SESSIONS.remove(session.getId());
    }

    public static int getNumberOfSessions(){
        return SESSIONS.size();
    }

    public static List<HttpSession> getAllSessions(){
        return new ArrayList<>(SESSIONS.values());
    }

    private SessionRegistry(){}

}
