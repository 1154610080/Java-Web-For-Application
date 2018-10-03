package com.web.config.annotation;

import org.springframework.stereotype.Controller;

import java.lang.annotation.*;

/**
 * 表示一个用于处理Restful Web服务请求的终端
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
public @interface RestEndPoint {
    String value() default "";
}
