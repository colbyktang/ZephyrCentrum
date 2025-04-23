package com.ctang.zephyrcentrum.security;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.services.AuthenticationService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;

@Component
public class JwtCookieFilter extends OncePerRequestFilter {

    private final AuthenticationService authenticationService;
    private final UserDetailsService userDetailsService;

    public JwtCookieFilter(
        AuthenticationService authenticationService,
        UserDetailsService userDetailsService
    ) {
        this.authenticationService = authenticationService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        System.out.println("Filter: Request to " + request.getRequestURI() + " with method " + request.getMethod());
        
        // Extract token from cookies
        String token = extractTokenFromCookies(request);
        
        // If token exists and is valid, set authentication
        if (token != null) {
            User user = authenticationService.validateToken(token);
            
            if (user != null) {
                // Load UserDetails and set authentication
                UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("AUTH_TOKEN".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        
        return null;
    }
}
