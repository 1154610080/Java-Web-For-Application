package com.web.site;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
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
public class SessionListener implements
        HttpSessionListener, HttpSessionIdListener, ServletContextListener{

    private static final Logger log = LogManager.getLogger();

    //当contextInitialized方法执行完成时，SessionRegistry的实现将被注入
    @Inject SessionRegistry sessionRegistry;

    @Override
    public void sessionIdChanged(HttpSessionEvent event, String oldSessionId) {
        log.debug("Session " + oldSessionId +" changed to " + event.getSession().getId());
        sessionRegistry.updateSession(event.getSession(), oldSessionId);
    }

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        log.debug("Session " + se.getSession().getId() + " created.");
        sessionRegistry.addSession(se.getSession());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        log.debug("Session " + se.getSession().getId() + "destroyed.");
        sessionRegistry.removeSession(se.getSession());
    }

    /**
     * 初始化应用上下文
     *      从ServletContext中获得根应用上下文，
     * 从应用上下文中获得bean工厂，并将SessionListener实例配置为根应用上下文的bean
     *
     * @date 2018/9/16 19:40
     * @param sce
     * @return void
     **/
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        WebApplicationContext context =
                WebApplicationContextUtils.getRequiredWebApplicationContext(
                        sce.getServletContext()
                );
        AutowireCapableBeanFactory factory =
                context.getAutowireCapableBeanFactory();
        factory.autowireBeanProperties(this,
                AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, true);
        factory.initializeBean(this, "sessionListener");
        log.info("Session listener initialized in Spring application context.");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}
