package com.web.config;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@Configurable
@EnableWebMvc
@ComponentScan(
        basePackages = "com.web.site",
        useDefaultFilters = false,
        includeFilters = @ComponentScan.Filter(Controller.class)
)

public class ServletContextConfiguration{

}
