package com.ctang.zephyrsanctum.controller;

import com.ctang.zephyrsanctum.exceptions.UsernameAlreadyExistsException;
import com.ctang.zephyrsanctum.models.User;
import com.ctang.zephyrsanctum.services.AuthenticationService;
import com.ctang.zephyrsanctum.services.UserService;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    
    public AuthController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody LoginRequest loginRequest) {
        try {
            // Authenticate and get token
            String token = authenticationService.authenticate(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            // Create response with token
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("token_type", "Bearer");
            
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "invalid_credentials");
            response.put("error_description", "Invalid username or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            // Other errors
            Map<String, Object> response = new HashMap<>();
            response.put("error", "authentication_error");
            response.put("error_description", "An error occurred during authentication");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Object> register(@Valid @RequestBody RegistrationRequest request) {
        try {
            User user = new User();
            user.setUsername (request.getUsername());
            user.setPlainPassword (request.getPassword());
            user.setEmail(request.getEmail());
            user = userService.createUser(user);
            
            // Auto-login after registration
            String token = authenticationService.authenticate(
                user.getUsername(), 
                request.getPassword()  // This would be the plain password before hashing
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("user", user);
            response.put("token", token);
            
            return ResponseEntity.ok(response);
        } catch (UsernameAlreadyExistsException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Username already exists");
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Registration failed");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    public static class LoginRequest {
        private String username;
        private String password;
        
        // Getters and setters
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
        
        public String getPassword() {
            return password;
        }
        
        public void setPassword(String password) {
            this.password = password;
        }
    }

    // Custom class for registration requests
    @Setter
    @Getter
    public static class RegistrationRequest {
        private String username;
        private String email;
        private String password;
    }
}