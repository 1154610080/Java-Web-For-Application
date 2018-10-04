package com.web.config;

import com.web.site.AuthenticationFilter;
import com.web.site.LoggingFilter;
import com.web.site.SessionListener;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.ws.transport.http.MessageDispatcherServlet;

import javax.servlet.*;

public class Bootstrap implements WebApplicationInitializer{
    @Override
    public void onStartup(ServletContext container) throws ServletException {

        container.getServletRegistration("default").addMapping("/resource/*");

        AnnotationConfigWebApplicationContext rootContext =
                new AnnotationConfigWebApplicationContext();
        rootContext.register(RootContextConfiguration.class);
        container.addListener(new ContextLoaderListener(rootContext));
        container.addListener(SessionListener.class);

        AnnotationConfigWebApplicationContext WebContext =
                new AnnotationConfigWebApplicationContext();
        WebContext.register(WebServletContextConfiguration.class);
        ServletRegistration.Dynamic dispatcher = container
                .addServlet("springDispatcher", new DispatcherServlet(WebContext));

        dispatcher.setLoadOnStartup(1);
        dispatcher.setMultipartConfig(new MultipartConfigElement(
                null, 20_971_520L, 41_943_040L, 512_000
        ));
        dispatcher.addMapping("/");

        AnnotationConfigWebApplicationContext restContext =
                new AnnotationConfigWebApplicationContext();
        restContext.register(RestServletContextConfiguration.class);
        DispatcherServlet servlet = new DispatcherServlet(restContext);
        servlet.setDispatchOptionsRequest(true);
        dispatcher = container.addServlet(
                "springRestDispatcher", servlet
        );
        dispatcher.setLoadOnStartup(2);
        dispatcher.addMapping("/services/Rest/*");

        AnnotationConfigWebApplicationContext soapContext =
                new AnnotationConfigWebApplicationContext();
        soapContext.register(SoapServletContextConfiguration.class);
        MessageDispatcherServlet soapServlet =
                new MessageDispatcherServlet(soapContext);
        soapServlet.setTransformWsdlLocations(true);
        dispatcher = container.addServlet("springSoapDispatcher", soapServlet);
        dispatcher.setLoadOnStartup(3);
        dispatcher.addMapping("/services/Soap/*");


        //注册日志过滤器
        FilterRegistration registration = container.addFilter("loggingFilter", new LoggingFilter());
        registration.addMappingForUrlPatterns(null, false, "/*");

        //注册验证过滤器
        registration = container.addFilter(
                "authenticationFilter", new AuthenticationFilter()
        );
        registration.addMappingForUrlPatterns(
                null, false, "/ticket","/ticket/*", "/chat", "/chat/*",
                "/session", "/session/*"
        );
    }
}
