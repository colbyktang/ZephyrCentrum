package com.ctang.zephyrcentrum.controller;

import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;

import com.ctang.zephyrcentrum.controller.UserController;
import com.ctang.zephyrcentrum.exceptions.UsernameAlreadyExistsException;
import com.ctang.zephyrcentrum.models.User;
import com.ctang.zephyrcentrum.services.UserService;
import com.ctang.zephyrcentrum.validators.UserUpdateValidator;
import com.ctang.zephyrcentrum.validators.UserValidator;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Bucket bucket;

    @Mock
    private ConsumptionProbe consumptionProbe;
    
    @Mock
    private UserValidator userValidator;
    
    @Mock
    private UserUpdateValidator userUpdateValidator;
    
    @Mock
    private WebDataBinder binder;

    @InjectMocks
    private UserController userController;

    private User testUser;
    private List<User> userList;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("user2");

        userList = Arrays.asList(testUser, user2);
    }

    @Test
    void getAllUsers_Success() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.getAllUsers()).thenReturn(userList);

        // Execute
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
        assertEquals("testuser", response.getBody().get(0).getUsername());
        verify(userService, times(1)).getAllUsers();
    }
    
    @Test
    void getAllUsers_EmptyList() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.getAllUsers()).thenReturn(List.of());

        // Execute
        ResponseEntity<List<User>> response = userController.getAllUsers();

        // Verify
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getAllUsers_RateLimitExceeded() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(false);
        when(consumptionProbe.getNanosToWaitForRefill()).thenReturn(1000000000L);

        // Execute & Verify
        ResponseEntity<List<User>> response = userController.getAllUsers();
        
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("X-Rate-Limit-Retry-After-Seconds"));
        verify(userService, never()).getAllUsers();
    }

    @Test
    void getUserById_Success() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(testUser);

        // Execute
        ResponseEntity<User> response = userController.getUserById(1L);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        verify(userService, times(1)).getUserById(1L);
    }
    
    @Test
    void getUserById_NotFound() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.getUserById(99L)).thenReturn(null);

        // Execute
        ResponseEntity<User> response = userController.getUserById(99L);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void getUserById_RateLimitExceeded() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(false);
        when(consumptionProbe.getNanosToWaitForRefill()).thenReturn(1000000000L);

        // Execute
        ResponseEntity<User> response = userController.getUserById(1L);

        // Verify
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("X-Rate-Limit-Retry-After-Seconds"));
        verify(userService, never()).getUserById(anyLong());
    }

    @Test
    void createUser_Success() throws UsernameAlreadyExistsException {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.createUser(testUser)).thenReturn(testUser);

        // Execute
        ResponseEntity<User> response = userController.createUser(testUser);

        // Verify
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("testuser", response.getBody().getUsername());
        verify(userService, times(1)).createUser(testUser);
    }

    @Test
    void createUser_RateLimitExceeded() throws UsernameAlreadyExistsException {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(false);
        when(consumptionProbe.getNanosToWaitForRefill()).thenReturn(1000000000L);

        // Execute
        ResponseEntity<User> response = userController.createUser(testUser);

        // Verify
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("X-Rate-Limit-Retry-After-Seconds"));
        verify(userService, never()).createUser(any());
    }
    
    @Test
    void createUser_UsernameAlreadyExists() throws UsernameAlreadyExistsException {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.createUser(testUser)).thenThrow(new UsernameAlreadyExistsException("testuser"));

        // Execute
        ResponseEntity<User> response = userController.createUser(testUser);

        // Verify
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userService, times(1)).createUser(testUser);
    }

    @Test
    void updateUser_Success() {
        // Setup
        Map<String, Object> fields = new HashMap<>();
        fields.put("username", "updatedUser");
        fields.put("email", "updated@example.com");
        
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");
        updatedUser.setEmail("updated@example.com");
        
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        doNothing().when(userUpdateValidator).validate(any(), any());
        when(userService.updateUser(eq(1L), eq(fields))).thenReturn(updatedUser);

        // Execute
        ResponseEntity<User> response = userController.updateUser(1L, fields);

        // Verify
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("updatedUser", response.getBody().getUsername());
        verify(userService, times(1)).updateUser(eq(1L), eq(fields));
    }
    
    @Test
    void updateUser_ValidationError() {
        // Setup
        Map<String, Object> fields = new HashMap<>();
        fields.put("email", "invalid-email");
        
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        
        // Mock validation to add errors
        doAnswer(invocation -> {
            Errors errors = invocation.getArgument(1);
            errors.rejectValue("email", "field.invalid", "Invalid email format");
            return null;
        }).when(userUpdateValidator).validate(any(), any());

        // Execute
        ResponseEntity<User> response = userController.updateUser(1L, fields);

        // Verify
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userService, never()).updateUser(anyLong(), anyMap());
    }

    @Test
    void updateUser_NotFound() {
        // Setup
        Map<String, Object> fields = new HashMap<>();
        fields.put("username", "updatedUser");
        
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        doNothing().when(userUpdateValidator).validate(any(), any());
        when(userService.updateUser(eq(99L), anyMap())).thenReturn(null);

        // Execute
        ResponseEntity<User> response = userController.updateUser(99L, fields);

        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser_RateLimitExceeded() {
        // Setup
        Map<String, Object> fields = new HashMap<>();
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(false);
        when(consumptionProbe.getNanosToWaitForRefill()).thenReturn(1000000000L);

        // Execute
        ResponseEntity<User> response = userController.updateUser(1L, fields);

        // Verify
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("X-Rate-Limit-Retry-After-Seconds"));
        verify(userService, never()).updateUser(anyLong(), anyMap());
    }

    @Test
    void deleteUser_Success() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.getUserById(1L)).thenReturn(testUser);
        
        // Execute
        ResponseEntity<Void> response = userController.deleteUser(1L);
        
        // Verify
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).deleteUser(1L);
    }
    
    @Test
    void deleteUser_NotFound() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(true);
        when(userService.getUserById(99L)).thenReturn(null);
        
        // Execute
        ResponseEntity<Void> response = userController.deleteUser(99L);
        
        // Verify
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(userService, never()).deleteUser(anyLong());
    }

    @Test
    void deleteUser_RateLimitExceeded() {
        // Setup
        when(bucket.tryConsumeAndReturnRemaining(1)).thenReturn(consumptionProbe);
        when(consumptionProbe.isConsumed()).thenReturn(false);
        when(consumptionProbe.getNanosToWaitForRefill()).thenReturn(1000000000L);

        // Execute
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Verify
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNotNull(response.getHeaders().getFirst("X-Rate-Limit-Retry-After-Seconds"));
        verify(userService, never()).deleteUser(anyLong());
    }
    
    @Test
    void testInitBinderMethod() throws Exception {
        // Use reflection to access the private method
        java.lang.reflect.Method method = UserController.class.getDeclaredMethod("initBinder", WebDataBinder.class);
        method.setAccessible(true);
        method.invoke(userController, binder);
        
        // Verify the binder adds the validator
        verify(binder).addValidators(userValidator);
    }
}