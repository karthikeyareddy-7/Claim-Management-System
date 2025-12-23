package com.example.claim_management_system.Claim_Management_System;

import com.example.claim_management_system.Claim_Management_System.Controllers.UserController;
import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserControllerTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User();
        user.setUserid(1);
        user.setUsername("testuser");
        user.setPassword("password123");
    }

    // ------------------- REGISTER TESTS -------------------
    @Test
    void testRegisterUserSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        ResponseEntity<String> response = userController.registerUser(user);

        assertEquals(201, response.getStatusCodeValue());
        assertEquals("User registered successfully!", response.getBody());
    }

    @Test
    void testRegisterUserUsernameExists() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<String> response = userController.registerUser(user);

        assertEquals(409, response.getStatusCodeValue());
        assertEquals("Username already exists", response.getBody());
    }

    // ------------------- LOGIN TESTS -------------------
    @Test
    void testLoginUserSuccess() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        ResponseEntity<String> response = userController.loginUser(loginRequest);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Login successful", response.getBody());
    }

    @Test
    void testLoginUserInvalidPassword() {
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

        User loginRequest = new User();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("wrongpassword");

        ResponseEntity<String> response = userController.loginUser(loginRequest);

        assertEquals(401, response.getStatusCodeValue());
        assertEquals("Invalid password", response.getBody());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        User loginRequest = new User();
        loginRequest.setUsername("nonexistent");
        loginRequest.setPassword("password123");

        ResponseEntity<String> response = userController.loginUser(loginRequest);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("User not found", response.getBody());
    }

    // ------------------- GET USER BY USERNAME -------------------
    @Test
    void testGetUserByUsernameSuccess() {
        when(userRepository.getUserByUsername("testuser")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUserByUsername("testuser");

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(user, response.getBody());
    }

    @Test
    void testGetUserByUsernameNotFound() {
        when(userRepository.getUserByUsername("nonexistent")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUserByUsername("nonexistent");

        assertEquals(404, response.getStatusCodeValue());
        assertNull(response.getBody());
    }
}
