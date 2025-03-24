package com.ctang.zephyrcentrum.validators;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.repositories.UserRepository;
import com.ctang.zephyrcentrum.types.Roles;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Component
public class UserUpdateValidator implements Validator, FieldUpdateValidator {
    
    private final UserRepository userRepository;
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    public UserUpdateValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.equals(clazz) || Map.class.isAssignableFrom(clazz);
    }

    @Override
    public boolean supportsField(String fieldName) {
        return Arrays.asList("username", "email", "password", "role").contains(fieldName);
    }
    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {
        if (target instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, Object> fields = (Map<String, Object>) target;
            validateFields(fields, errors);
        }
    }

    @Override
    public void validateFields(Map<String, Object> fields, Errors errors) {
        // Get the user ID from the request context if needed
        Long userId = (Long) fields.get("_userId"); // This would need to be set in the controller
        
        for (Map.Entry<String, Object> entry : fields.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            
            if (!supportsField(key) || key.equals("_userId")) {
                continue;
            }
            
            switch (key) {
                case "username":
                    validateUsername((String) value, userId, errors);
                    break;
                case "email":
                    validateEmail((String) value, userId, errors);
                    break;
                case "password":
                    validatePassword((String) value, errors);
                    break;
                case "role":
                    validateRole((String) value, errors);
                    break;
            }
        }
    }
    
    private void validateUsername(String username, Long userId, Errors errors) {
        if (username == null || username.trim().isEmpty()) {
            errors.rejectValue("username", "field.required", "Username is required");
            return;
        }
        
        if (username.length() < 3 || username.length() > 30) {
            errors.rejectValue("username", "field.length", 
                "Username must be between 3 and 30 characters");
            return;
        }
        
        // Check if username is already taken by another user
        Optional<User> existingUser = userRepository.findByUsername(username);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            errors.rejectValue("username", "field.duplicate", "Username is already taken");
        }
    }
    
    private void validateEmail(String email, Long userId, Errors errors) {
        if (email == null || email.trim().isEmpty()) {
            errors.rejectValue("email", "field.required", "Email is required");
            return;
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            errors.rejectValue("email", "field.invalid", "Invalid email format");
            return;
        }
        
        // Check if email is already taken by another user
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().getId().equals(userId)) {
            errors.rejectValue("email", "field.duplicate", "Email is already taken");
        }
    }
    
    private void validatePassword(String password, Errors errors) {
        if (password == null || password.trim().isEmpty()) {
            errors.rejectValue("password", "field.required", "Password is required");
            return;
        }
        
        if (password.length() < 8) {
            errors.rejectValue("password", "field.min.length", 
                "Password must be at least 8 characters long");
        }
        
        // Check for password complexity if needed
        boolean hasLetter = password.matches(".*[a-zA-Z].*");
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[^a-zA-Z0-9].*");
        
        if (!(hasLetter && hasDigit && hasSpecial)) {
            errors.rejectValue("password", "field.complexity", 
                "Password must contain letters, numbers, and special characters");
        }
    }
    
    private void validateRole(String roleStr, Errors errors) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            errors.rejectValue("role", "field.required", "Role is required");
            return;
        }
        
        try {
            Roles role = Roles.valueOf(roleStr.toUpperCase());
            if (role == null) {
                errors.rejectValue("role", "field.invalid", "Invalid role");
            }
        } catch (IllegalArgumentException e) {
            errors.rejectValue("role", "field.invalid", 
                "Invalid role. Must be one of: " + Arrays.toString(Roles.values()));
        }
    }
}