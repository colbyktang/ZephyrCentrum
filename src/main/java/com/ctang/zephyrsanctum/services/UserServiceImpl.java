package com.ctang.zephyrsanctum.services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ctang.zephyrsanctum.repositories.UserRepository;
import com.ctang.zephyrsanctum.types.Roles;
import com.ctang.zephyrsanctum.exceptions.UsernameAlreadyExistsException;
import com.ctang.zephyrsanctum.models.User;

@Service
public class UserServiceImpl implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @Override
    public User getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User createUser(User user) throws UsernameAlreadyExistsException {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            throw new UsernameAlreadyExistsException(existingUser.get().getUsername());
        }

        // Handle password hashing
        if (user.getPlainPassword() != null) {
            user.setPasswordHash(passwordEncoder.encode(user.getPlainPassword()));
            // Clear the plain password for security
            user.setPlainPassword(null);
        } else if (user.getPasswordHash() != null) {
            // If direct passwordHash is provided, encode it (migration path)
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(Long id, Map<String, Object> fields) {
        Optional<User> existingUserOptional = userRepository.findById(id);
        if (!existingUserOptional.isPresent()) {
            return null;
        }

        User user = existingUserOptional.get();

        // Update only the fields provided in the map
        fields.forEach((key, value) -> {
            if (value == null || key.equals("_userId")) {
                return; // Skip null values and internal fields
            }
            
            switch (key) {
                case "username":
                    user.setUsername((String) value);
                    break;
                case "email":
                    user.setEmail((String) value);
                    break;
                case "password":
                    user.setPasswordHash(passwordEncoder.encode((String) value));
                    break;
                case "role":
                    try {
                        Roles role = Roles.valueOf(((String) value).toUpperCase());
                        user.setRole(role);
                    } catch (IllegalArgumentException ignored) {
                        // Invalid role - should be caught by validator
                    }
                    break;
            }
        });
        
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.delete(user.get());
        }
        else {
            throw new RuntimeException("User " + id + " does not exist in the database!");
        }
    }
}
