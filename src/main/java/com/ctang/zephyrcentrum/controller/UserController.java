package com.ctang.zephyrcentrum.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import com.ctang.zephyrcentrum.exceptions.UsernameAlreadyExistsException;
import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.services.UserService;
import com.ctang.zephyrcentrum.validators.UserUpdateValidator;
import com.ctang.zephyrcentrum.validators.UserValidator;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final Bucket bucket;
    private final UserValidator userValidator;
    private final UserUpdateValidator userUpdateValidator;  // Add this

    @InitBinder("user")
    protected void initBinder(WebDataBinder binder) {
        binder.addValidators(userValidator);
    }

    @InitBinder("fields")  // Only apply to model attributes named "fields" 
    protected void initUpdateBinder(WebDataBinder binder) {
        binder.addValidators(userUpdateValidator);
    }
    
    public UserController(UserService userService, Bucket bucket, 
                         UserValidator userValidator, 
                         UserUpdateValidator userUpdateValidator) {  // Update constructor
        this.userService = userService;
        this.bucket = bucket;
        this.userValidator = userValidator;
        this.userUpdateValidator = userUpdateValidator;
    }

    @GetMapping
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<User>> getAllUsers() {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            List<User> users = userService.getAllUsers();
            return users.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/{id}")
    // @PreAuthorize("hasRole('USER')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            User user = userService.getUserById(id);
            return user == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    @PostMapping
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) throws UsernameAlreadyExistsException {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        try {
            User newUser = userService.createUser(user);
            return newUser == null ? ResponseEntity.badRequest().build() : ResponseEntity.ok(newUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PatchMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }
        
        try {
            User updatedUser = userService.updateUser(id, fields);
            return updatedUser == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@Valid @PathVariable Long id) {
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
            .header("X-Rate-Limit-Retry-After-Seconds", 
                        String.valueOf(probe.getNanosToWaitForRefill() / 1_000_000_000))
                .build();
        }

        try {
            User user = userService.getUserById(id);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }

            userService.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}