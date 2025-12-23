package com.example.claim_management_system.Claim_Management_System;

import com.example.claim_management_system.Claim_Management_System.Controllers.ClaimController;
import com.example.claim_management_system.Claim_Management_System.Objects.Claim;
import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;
import com.example.claim_management_system.Claim_Management_System.Services.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ClaimControllerTest {

    @Mock
    private ClaimService claimService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ClaimController claimController;

    private User user;
    private Claim claim;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Setup user
        user = new User();
        user.setUserid(1);
        user.setUsername("testuser");
        user.setDob(LocalDate.of(1990, 1, 1));

        // Setup claim
        claim = new Claim();
        claim.setClaimid(1);
        claim.setDescriptioncode("Desc");
        claim.setProcedurecode("Proc");
        claim.setPrice(BigDecimal.valueOf(100));
        claim.setUser(user);
        claim.setDateSubmitted(LocalDateTime.now());
    }

    @Test
    void testCreateClaimSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(claimService.createClaim(claim, 1)).thenReturn(claim);

        ResponseEntity<?> response = claimController.createClaim(1, claim);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertNotNull(body);
        assertEquals("Desc", body.get("descriptioncode"));
        assertEquals(1, body.get("userid"));
    }

    @Test
    void testCreateClaimUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = claimController.createClaim(1, claim);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testGetAllClaimsEmpty() {
        when(claimService.getAllClaims()).thenReturn(Collections.emptyList());

        ResponseEntity<List<Map<String, Object>>> response = claimController.getAllClaims();

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testGetAllClaimsNotEmpty() {
        // Use Java 8 safe list creation
        Claim claim1 = new Claim();
        claim1.setClaimid(1);
        claim1.setDescriptioncode("Test Claim");
        claim1.setUser(user);

        List<Claim> claimList = Arrays.asList(claim1);

        when(claimService.getAllClaims()).thenReturn(claimList);

        ResponseEntity<List<Map<String, Object>>> response = claimController.getAllClaims();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(claimList.size(), response.getBody().size());
        assertEquals("Test Claim", response.getBody().get(0).get("descriptioncode"));
    }

    @Test
    void testGetClaimsByUserSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        List<Claim> userClaims = Arrays.asList(claim);
        when(claimService.getClaimsByUserId(1)).thenReturn(userClaims);

        ResponseEntity<?> response = claimController.getClaimsByUser(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> body = (List<Map<String, Object>>) response.getBody();
        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals("Desc", body.get(0).get("descriptioncode"));
    }

    @Test
    void testGetClaimsByUserNotFound() {
        when(userRepository.findById(1)).thenReturn(Optional.empty());

        ResponseEntity<?> response = claimController.getClaimsByUser(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testUpdateClaimSuccess() {
        Claim updatedClaim = new Claim();
        updatedClaim.setClaimid(1);
        updatedClaim.setDescriptioncode("Updated");
        updatedClaim.setProcedurecode("UpdatedProc");
        updatedClaim.setPrice(BigDecimal.valueOf(200));
        updatedClaim.setUser(user);
        updatedClaim.setDateSubmitted(LocalDateTime.now());

        when(claimService.updateClaim(1, updatedClaim)).thenReturn(updatedClaim);

        ResponseEntity<?> response = claimController.updateClaim(1, updatedClaim);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Updated", body.get("descriptioncode"));
    }

    @Test
    void testUpdateClaimNotFound() {
        when(claimService.updateClaim(1, claim)).thenThrow(new RuntimeException("Claim not found"));

        ResponseEntity<?> response = claimController.updateClaim(1, claim);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Claim not found", response.getBody());
    }

    @Test
    void testDeleteClaimSuccess() {
        doNothing().when(claimService).deleteClaim(1);

        ResponseEntity<?> response = claimController.deleteClaim(1);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Claim deleted successfully", response.getBody());
    }

    @Test
    void testDeleteClaimNotFound() {
        doThrow(new RuntimeException("Claim not found")).when(claimService).deleteClaim(1);

        ResponseEntity<?> response = claimController.deleteClaim(1);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Claim not found", response.getBody());
    }
}
