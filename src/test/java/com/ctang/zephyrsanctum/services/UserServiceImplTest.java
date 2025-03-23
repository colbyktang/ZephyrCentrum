package com.ctang.zephyrsanctum.services;

import com.ctang.zephyrsanctum.models.User;
import com.ctang.zephyrsanctum.repositories.UserRepository;
import com.ctang.zephyrsanctum.types.Roles;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private PasswordEncoder passwordEncoder;
    
    private UserServiceImpl userService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, passwordEncoder);
    }
    
    @Test
    void testGetUserById() {
        // Arrange
        User expectedUser = new User();
        expectedUser.setId(1L);
        expectedUser.setUsername("testuser");
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(expectedUser));
        
        // Act
        User actualUser = userService.getUserById(1L);
        
        // Assert
        assertNotNull(actualUser);
        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getUsername(), actualUser.getUsername());
        verify(userRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetUserByIdNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        
        // Act
        User actualUser = userService.getUserById(99L);
        
        // Assert
        assertNull(actualUser);
        verify(userRepository, times(1)).findById(99L);
    }
    
    @Test
    void testGetAllUsers() {
        // Arrange
        List<User> expectedUsers = Arrays.asList(
            createTestUser(1L, "user1", "user1@example.com"),
            createTestUser(2L, "user2", "user2@example.com")
        );
        
        when(userRepository.findAll()).thenReturn(expectedUsers);
        
        // Act
        List<User> actualUsers = userService.getAllUsers();
        
        // Assert
        assertEquals(2, actualUsers.size());
        assertEquals(expectedUsers.get(0).getId(), actualUsers.get(0).getId());
        assertEquals(expectedUsers.get(1).getUsername(), actualUsers.get(1).getUsername());
        verify(userRepository, times(1)).findAll();
    }
    
    // Helper method to create test users
    private User createTestUser(Long id, String username, String email) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setEmail(email);
        user.setRole(Roles.USER);
        return user;
    }
}