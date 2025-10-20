package com.assignment.common.validator;

import com.assignment.common.exception.CustomException;
import com.assignment.common.model.ResponseCode;
import com.assignment.domain.user.entity.User;
import com.assignment.domain.user.repository.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class XUserValidator implements ConstraintValidator<XUserId, Long> {
    private boolean optional;
    private final UserRepository userRepository;

    @Override
    public void initialize(XUserId constraintAnnotation) {
        this.optional = constraintAnnotation.optional();
    }

    @Override
    public boolean isValid(Long userId, ConstraintValidatorContext context) {
        if (optional && userId == null) return true;

        if(userId < 1) {
            throw new CustomException(ResponseCode.INVALID_X_USER_ID);
        }

        Optional<User> user = userRepository.findById(userId);

        if (!(user.isPresent())) {
            throw new CustomException(ResponseCode.INVALID_X_USER_ID);
        }

        return true;
    }
}
