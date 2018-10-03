package com.web.config.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * 表示控制器用于处理传统Web请求
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface WebController {
    String value() default "";
}
