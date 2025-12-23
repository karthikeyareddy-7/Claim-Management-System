package com.example.claim_management_system.Claim_Management_System;

import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;
import com.example.claim_management_system.Claim_Management_System.Services.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserid(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setFirstname("John");
        user.setLastname("Doe");
        user.setEmail("john.doe@example.com");
        user.setDob(LocalDate.of(1990, 1, 1));
    }

    // ========================= REGISTER =========================
    @Test
    void testRegisterUserSuccess() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        User registered = userService.registerUser(user);

        assertEquals("testuser", registered.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testRegisterUserAlreadyExists() {
        when(userRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.registerUser(user));
        assertEquals("Username already exists", ex.getMessage());
    }

    // ========================= UPDATE =========================
    @Test
    void testUpdateUserSuccess() {
        User updated = new User();
        updated.setFirstname("Jane");
        updated.setLastname("Smith");
        updated.setEmail("jane.smith@example.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User result = userService.updateUser(1, updated);

        assertEquals("Jane", result.getFirstname());
        assertEquals("Smith", result.getLastname());
        assertEquals("jane.smith@example.com", result.getEmail());
    }

    @Test
    void testUpdateUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> userService.updateUser(1, user));
        assertEquals("User not found", ex.getMessage());
    }

    // ========================= DELETE =========================
    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).deleteById(1);
        assertDoesNotThrow(() -> userService.deleteUser(1));
        verify(userRepository, times(1)).deleteById(1);
    }

    // ========================= GET USER =========================
    @Test
    void testGetUserByIdFound() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        Optional<User> found = userService.getUserById(1);
        assertTrue(found.isPresent());
        assertEquals(user, found.get());
    }

    @Test
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());
        Optional<User> found = userService.getUserById(1);
        assertTrue(found.isEmpty());
    }

    @Test
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        List<User> users = userService.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(user, users.get(0));
    }

    // ========================= LOGIN =========================
    @Test
    void testLoginSuccess() {
        when(userRepository.findByUsernameAndPassword("testuser", "password")).thenReturn(Optional.of(user));

        User loggedIn = userService.login("testuser", "password");
        assertEquals(user, loggedIn);
    }

    @Test
    void testLoginInvalidCredentials() {
        when(userRepository.findByUsernameAndPassword("testuser", "wrong")).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> userService.login("testuser", "wrong"));
        assertEquals("Invalid username or password", ex.getMessage());
    }

    // ========================= CALCULATE AGE =========================
    @Test
    void testCalculateAge() {
        int age = userService.calculateAge(user);
        int expectedAge = LocalDate.now().getYear() - 1990;
        assertEquals(expectedAge, age);
    }
}
