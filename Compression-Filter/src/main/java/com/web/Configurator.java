package com.web;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

//配置过滤器
@WebListener
public class Configurator implements ServletContextListener{

    @Override
    public void contextInitialized(ServletContextEvent sce) {

        ServletContext context = sce.getServletContext();

        FilterRegistration.Dynamic registration = context.addFilter
                ("requestLogFilter", new RequestLogFilter());
        registration.addMappingForUrlPatterns
                (null, false, "/*");

        registration = context.addFilter
                ("compressionFilter", new CompressionFilter());
        registration.setAsyncSupported(true);
        registration.addMappingForUrlPatterns
                (null, false, "/*");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) { }
}
