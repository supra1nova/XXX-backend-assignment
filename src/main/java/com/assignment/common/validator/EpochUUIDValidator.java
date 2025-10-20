package com.assignment.common.validator;

import com.assignment.common.exception.CustomException;
import com.assignment.common.model.ResponseCode;
import io.micrometer.common.util.StringUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.regex.Pattern;

@RequiredArgsConstructor
public class EpochUUIDValidator implements ConstraintValidator<EpochUUID, String> {
    private boolean optional;
//    private static final Pattern UUID_PATTERN =
//        Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-7[0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    private static final Pattern UUID_PATTERN =
        Pattern.compile("^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[123457][0-9a-fA-F]{3}-[89abAB][0-9a-fA-F]{3}-[0-9a-fA-F]{12}$");

    @Override
    public void initialize(EpochUUID constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (optional && StringUtils.isBlank(value)) return true;

        if (StringUtils.isBlank(value)) {
            throw new CustomException(ResponseCode.INVALID_FRIEND_REQUEST);
        }
        if (!UUID_PATTERN.matcher(value).matches()) {
            throw new CustomException(ResponseCode.INVALID_FRIEND_REQUEST);
        }
        return true;
    }
}
