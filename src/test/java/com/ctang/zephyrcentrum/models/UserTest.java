package com.ctang.zephyrcentrum.models;

import org.junit.jupiter.api.Test;

import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.types.Roles;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    
    @Test
    void testUserCreation() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRole(Roles.USER);
        user.setPlainPassword("password");
        
        assertEquals("testuser", user.getUsername());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(Roles.USER, user.getRole());
        assertEquals("password", user.getPlainPassword());
    }
    
    @Test
    void testUserEquality() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser");
        user1.setEmail("test@example.com");
        
        User user2 = new User();
        user2.setId(1L);
        user2.setUsername("testuser");
        user2.setEmail("test@example.com");
        
        assertEquals(user1, user2);
        assertEquals(user1.hashCode(), user2.hashCode());
    }
    
    @Test
    void testUserInequality() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("testuser1");
        
        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("testuser2");
        
        assertNotEquals(user1, user2);
    }
}