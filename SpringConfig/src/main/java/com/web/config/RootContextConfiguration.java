package com.web.config;


import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;

@Configurable
@ComponentScan(
        basePackages = "com.web.site",
        excludeFilters = @ComponentScan.Filter(Controller.class)
)


public class RootContextConfiguration {
}
