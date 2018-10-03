package com.web.config.annotation;

import org.springframework.web.bind.annotation.ControllerAdvice;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ControllerAdvice
public @interface RestEndPointAdvice {
    String value() default "";
}
