package com.ctang.zephyrcentrum.validators;

import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.ctang.zephyrcentrum.models.User;

import org.springframework.lang.NonNull;

@Component
public class UserValidator implements Validator {

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.equals(clazz);
    }
    
    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        User user = (User) target;

        // Check username
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "field.required", "Username is required");
        if (user.getUsername() != null && user.getUsername().length() < 3) {
            errors.rejectValue("username", "field.min.length", "Username must be at least 3 characters long");
        }

        // Check email
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "field.required", "Email is required");
        // Add your custom email validation logic if needed

        // Check password
        if (user.getPasswordHash() == null || user.getPasswordHash().isEmpty()) {
            errors.rejectValue("passwordHash", "field.required", "Password is required");
        }
    }
}