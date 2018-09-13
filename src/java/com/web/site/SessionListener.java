package com.web.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionIdListener;
import javax.servlet.http.HttpSessionListener;

/**
 * 会话监听器
 * 负责捕捉并处理会话创建、销毁和迁移事件
 *
 * @author Egan
 * @date 2018/9/13 22:16
 **/
@WebListener
public class SessionListener implements HttpSessionListener, HttpSessionIdListener{

    private static final Logger log = LogManager.getLogger();

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        log.debug("Session " + oldSessionId +" changed to " + event.getSession().getId());
        SessionRegistry.updateSession(event.getSession(), oldSessionId);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.debug("Session " + se.getSession().getId() + " created.");
        SessionRegistry.addSession(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.debug("Session " + se.getSession().getId() + "destroyed.");
        SessionRegistry.removeSession(se.getSession());
    }
}
