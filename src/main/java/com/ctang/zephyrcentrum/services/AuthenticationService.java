package com.ctang.zephyrcentrum.services;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.ctang.zephyrcentrum.models.User;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class AuthenticationService {

    private final UserService userService;
    private final JwtEncoder jwtEncoder;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(UserService userService, JwtEncoder jwtEncoder, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtEncoder = jwtEncoder;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Authenticates a user and returns a JWT token
     *
     * @param username The username
     * @param password The raw password
     * @return JWT token if authentication is successful
     * @throws BadCredentialsException if authentication fails
     */
    public String authenticate(String username, String password) {
        // Get user from database
        User user = userService.getUserByUsername(username);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Verify password
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid username or password");
        }

        // Successfully authenticated - create JWT token
        Instant now = Instant.now();

        // Create scope string from authorities
        String scope = "ROLE_" + user.getRole().name();
        
        // Build JWT claims
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(1, ChronoUnit.HOURS))
                .subject(user.getUsername())
                .claim("scope", scope)
                .claim("userId", user.getId()) // Add user ID for resource access
                .build();
        
        // Generate and return token
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}