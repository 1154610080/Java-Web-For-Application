package com.web.config;

import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

public class Bootstrap implements WebApplicationInitializer{
    @Override
    public void onStartup(ServletContext container) throws ServletException {

        container.getServletRegistration("default").addMapping("/resource/*");

        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(RootContextConfiguration.class);
        container.addListener(new ContextLoaderListener(rootContext));

        AnnotationConfigWebApplicationContext sevletContext =
                new AnnotationConfigWebApplicationContext();
        sevletContext.register(ServletContextConfiguration.class);
        ServletRegistration.Dynamic dispatcher = container.addServlet(
                "SpringDispatcher", new DispatcherServlet(sevletContext));

        dispatcher.addMapping("/");
        dispatcher.setLoadOnStartup(1);
    }
}
