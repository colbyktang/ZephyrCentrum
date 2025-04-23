package com.ctang.zephyrcentrum.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.web.bind.annotation.*;

import com.ctang.zephyrcentrum.exceptions.UsernameAlreadyExistsException;
import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.services.AuthenticationService;
import com.ctang.zephyrcentrum.services.UserService;

import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final CsrfTokenRepository csrfTokenRepository;
    
    public AuthController(
        AuthenticationService authenticationService, 
        UserService userService,
        CsrfTokenRepository csrfTokenRepository
    ) {
        this.authenticationService = authenticationService;
        this.userService = userService;
        this.csrfTokenRepository = csrfTokenRepository;
    }
    
    @GetMapping("/csrf-token")
    public ResponseEntity<Map<String, String>> getCsrfToken(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("CSRF token requested: " + request.getHeaderNames());
        CsrfToken csrfToken = csrfTokenRepository.generateToken(request);
        csrfTokenRepository.saveToken(csrfToken, request, response);
        
        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("csrfToken", csrfToken.getToken());
        
        return ResponseEntity.ok(responseMap);
    }
    
    @PostMapping("/login")
    public ResponseEntity<Object> login(
        @RequestBody LoginRequest loginRequest,
        HttpServletRequest request,
        HttpServletResponse response
    ) {
        try {
            System.out.println("Login request received for username: " + loginRequest.getUsername());
            System.out.println("Request method: " + request.getMethod());
            System.out.println("Request headers: " + request.getHeaderNames());
            // Authenticate and get token
            String token = authenticationService.authenticate(
                loginRequest.getUsername(), 
                loginRequest.getPassword()
            );
            
            // Set JWT in HTTP-Only cookie
            Cookie authCookie = new Cookie("AUTH_TOKEN", token);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(true); // Enable in production
            authCookie.setPath("/");
            authCookie.setMaxAge(3600); // 1 hour in seconds
            response.addCookie(authCookie);
            
            // Get the user to return in response
            User user = userService.getUserByUsername(loginRequest.getUsername());
            
            // Create response with user data (but NO token since it's in the cookie)
            Map<String, Object> responseBody = new HashMap<>();
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            
            Map<String, Object> dataWrapper = new HashMap<>();
            dataWrapper.put("user", userData);
            
            responseBody.put("data", dataWrapper);
            
            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "invalid_credentials");
            responseBody.put("message", "Invalid username or password");

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(responseBody);
        } catch (Exception e) {
            // Other errors
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("error", "authentication_error");
            responseBody.put("message", "An error occurred during authentication");
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<Object> register(
        @Valid @RequestBody RegistrationRequest request,
        HttpServletRequest httpRequest,
        HttpServletResponse httpResponse
    ) {
        try {
            User user = new User();
            user.setUsername(request.getUsername());
            user.setPlainPassword(request.getPassword());
            user.setEmail(request.getEmail());
            user = userService.createUser(user);
            
            // Auto-login after registration
            String token = authenticationService.authenticate(
                user.getUsername(), 
                request.getPassword()  // This would be the plain password before hashing
            );
            
            // Set JWT in HTTP-Only cookie
            Cookie authCookie = new Cookie("AUTH_TOKEN", token);
            authCookie.setHttpOnly(true);
            authCookie.setSecure(true); // Enable in production
            authCookie.setPath("/");
            authCookie.setMaxAge(3600); // 1 hour in seconds
            httpResponse.addCookie(authCookie);
            
            // Create response with user data (but NO token since it's in the cookie)
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("username", user.getUsername());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole());
            
            Map<String, Object> dataWrapper = new HashMap<>();
            dataWrapper.put("user", userData);
            
            Map<String, Object> responseBody = new HashMap<>();
            responseBody.put("data", dataWrapper);
            
            return ResponseEntity.ok(responseBody);
        } catch (UsernameAlreadyExistsException e) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("error", "username_exists");
            responseBody.put("message", "Username already exists");
            return ResponseEntity.badRequest().body(responseBody);
        } catch (Exception e) {
            Map<String, String> responseBody = new HashMap<>();
            responseBody.put("error", "registration_failed");
            responseBody.put("message", "Registration failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(responseBody);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Object> logout(HttpServletResponse response) {
        // Clear the auth cookie
        Cookie authCookie = new Cookie("AUTH_TOKEN", null);
        authCookie.setHttpOnly(true);
        authCookie.setSecure(true); // Enable in production
        authCookie.setPath("/");
        authCookie.setMaxAge(0); // Delete the cookie
        response.addCookie(authCookie);
        
        return ResponseEntity.ok().body(Map.of("message", "Logged out successfully"));
    }
    
    @GetMapping("/check-auth")
    public ResponseEntity<Object> checkAuthentication(@CookieValue(name = "AUTH_TOKEN", required = false) String token) {
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
        }
        
        try {
            // Validate the token and get user information
            User user = authenticationService.validateToken(token);
            
            if (user != null) {
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", user.getId());
                userData.put("username", user.getUsername());
                userData.put("email", user.getEmail());
                userData.put("role", user.getRole());
                
                Map<String, Object> dataWrapper = new HashMap<>();
                dataWrapper.put("user", userData);
                
                Map<String, Object> responseBody = new HashMap<>();
                responseBody.put("data", dataWrapper);
                
                return ResponseEntity.ok(responseBody);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("authenticated", false));
        }
    }
    
    // Login request class (unchanged)
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

    // Registration request class (unchanged)
    @Setter
    @Getter
    public static class RegistrationRequest {
        private String username;
        private String email;
        private String password;
    }
}