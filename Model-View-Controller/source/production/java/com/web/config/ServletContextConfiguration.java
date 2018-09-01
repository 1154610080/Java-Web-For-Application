package com.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

@Configuration
@EnableWebMvc
@ComponentScan(
        basePackages = "com.web.site",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(Controller.class)
)
public class ServletContextConfiguration {

    /**
     * 配置视图名称转换
     * 去除Web应用上下文URL和URL结尾的文件扩展名，
     * 剩下的URL变成视图名称
     * 如 http://localhost:8080/mvc/foo/bar.html将转换成视图名称 foo/bar
     **/
    @Bean
    public RequestToViewNameTranslator viewNameTranslator()
    {
        return new DefaultRequestToViewNameTranslator();
    }

    /**
     * 配置视图解析
     * 将视图名称转换成JSP文件名
     **/
    @Bean
    public ViewResolver viewResolver(){
        InternalResourceViewResolver resolver =
                new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/WEB-INF/jsp/view/");
        resolver.setSuffix(".jsp");
        return resolver;
    }

}
