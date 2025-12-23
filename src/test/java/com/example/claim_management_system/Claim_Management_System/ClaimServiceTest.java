package com.example.claim_management_system.Claim_Management_System;

import com.example.claim_management_system.Claim_Management_System.Objects.Claim;
import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Repositories.ClaimRepository;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;
import com.example.claim_management_system.Claim_Management_System.Services.ClaimService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClaimServiceTest {

    @Mock
    private ClaimRepository claimRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClaimService claimService;

    private User user;
    private Claim claim;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUserid(1);
        user.setUsername("testuser");
        user.setPassword("password");
        user.setDob(LocalDate.of(1990, 1, 1));

        claim = new Claim();
        claim.setClaimid(1);
        claim.setDescriptioncode("Desc");
        claim.setProcedurecode("Proc");
        claim.setPrice(BigDecimal.valueOf(100.0));
        claim.setUser(user);
        claim.setDateSubmitted(LocalDateTime.now());
    }

    @Test
    void testCreateClaimSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Claim saved = claimService.createClaim(claim, 1);

        assertEquals("Desc", saved.getDescriptioncode());
        assertNotNull(saved.getDateSubmitted());
        assertEquals(user, saved.getUser());
    }

    @Test
    void testCreateClaimUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> claimService.createClaim(claim, 1));
        assertEquals("User not found", ex.getMessage());
    }

    @Test
    void testUpdateClaimSuccess() {
        when(claimRepository.findById(1)).thenReturn(Optional.of(claim));
        when(claimRepository.save(any(Claim.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Claim updated = new Claim();
        updated.setDescriptioncode("Updated");
        updated.setProcedurecode("UpdatedProc");
        updated.setPrice(BigDecimal.valueOf(200.0));

        Claim result = claimService.updateClaim(1, updated);

        assertEquals("Updated", result.getDescriptioncode());
        assertEquals("UpdatedProc", result.getProcedurecode());
        assertEquals(BigDecimal.valueOf(200.0), result.getPrice());
    }

    @Test
    void testUpdateClaimNotFound() {
        when(claimRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> claimService.updateClaim(1, claim));
        assertEquals("Claim not found", ex.getMessage());
    }

    @Test
    void testDeleteClaimSuccess() {
        when(claimRepository.existsById(1)).thenReturn(true);
        doNothing().when(claimRepository).deleteById(1);

        assertDoesNotThrow(() -> claimService.deleteClaim(1));
        verify(claimRepository, times(1)).deleteById(1);
    }

    @Test
    void testDeleteClaimNotFound() {
        when(claimRepository.existsById(1)).thenReturn(false);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> claimService.deleteClaim(1));
        assertEquals("Claim not found", ex.getMessage());
    }

    @Test
    void testGetClaimById() {
        when(claimRepository.findById(1)).thenReturn(Optional.of(claim));
        Optional<Claim> found = claimService.getClaimById(1);
        assertTrue(found.isPresent());
        assertEquals(claim, found.get());
    }

    @Test
    void testGetAllClaims() {
        when(claimRepository.findAll()).thenReturn(List.of(claim));
        List<Claim> list = claimService.getAllClaims();
        assertEquals(1, list.size());
    }

    @Test
    void testGetClaimsByUserId() {
        when(claimRepository.findByUser_Userid(1)).thenReturn(List.of(claim));
        List<Claim> list = claimService.getClaimsByUserId(1);
        assertEquals(1, list.size());
        assertEquals(claim, list.get(0));
    }
}
