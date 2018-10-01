package com.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.ReportAsSingleViolation;
import javax.validation.constraints.NotNull;
import java.lang.annotation.*;

/**
 * 自定义非空约束注解
 *
 * @author Egan
 * @date 2018/9/28 17:04
 **/
@SuppressWarnings("unused")
@Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.METHOD,
        ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@Documented
@Constraint(validatedBy = {NotBlankValidator.class})
@ReportAsSingleViolation
public @interface NotBlank {
    String message() default "com.web.validation.NotBlank.message";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    @Target({ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER,
            ElementType.METHOD, ElementType.FIELD})
    static @interface List{
            NotBlank[] value();
    }
}
