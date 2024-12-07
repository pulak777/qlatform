package com.qlatform.quant.util.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null) {
            return false;
        }

        boolean hasLength = password.length() >= 8;
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasNumber = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[@$!%*?&].*");

        boolean valid = hasLength && hasUpper && hasLower && hasNumber && hasSpecial;

        if (!valid) {
            context.disableDefaultConstraintViolation();
            StringBuilder message = new StringBuilder("Password must ");
            if (!hasLength) {
                message.append("be at least 8 characters long, ");
            }
            if (!hasUpper) {
                message.append("contain at least one uppercase letter, ");
            }
            if (!hasLower) {
                message.append("contain at least one lowercase letter, ");
            }
            if (!hasNumber) {
                message.append("contain at least one number, ");
            }
            if (!hasSpecial) {
                message.append("contain at least one special character (@$!%*?&), ");
            }
            message.setLength(message.length() - 2);  // Remove last comma and space

            context.buildConstraintViolationWithTemplate(message.toString())
                    .addConstraintViolation();
        }

        return valid;
    }
}
