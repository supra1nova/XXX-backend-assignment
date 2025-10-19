package com.assignment.common.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = XUserValidator.class)
@Documented
public @interface XUserId {
    String message() default "올바르지 않은 X-USER-ID 입니다.";

    boolean optional() default false;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
