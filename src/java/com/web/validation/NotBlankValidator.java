package com.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 非空约束器
 *
 * @author Egan
 * @date 2018/9/28 17:08
 **/
public class NotBlankValidator implements ConstraintValidator<NotBlank, CharSequence> {
    @Override
    public void initialize(NotBlank notBlank) {

    }

    @Override
    public boolean isValid(CharSequence charSequence, ConstraintValidatorContext constraintValidatorContext) {
        if(charSequence instanceof String)
            return ((String)charSequence).trim().length() > 0;
        return charSequence.toString().trim().length() > 0;
    }
}
