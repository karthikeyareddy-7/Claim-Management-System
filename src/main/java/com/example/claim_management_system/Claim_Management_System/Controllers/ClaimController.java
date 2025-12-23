package com.example.claim_management_system.Claim_Management_System.Controllers;

import com.example.claim_management_system.Claim_Management_System.Objects.Claim;
import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Services.ClaimService;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/claims")
@CrossOrigin(origins = "http://localhost:4200") // allow Angular frontend
public class ClaimController {

    @Autowired
    private ClaimService claimService;

    @Autowired
    private UserRepository userRepository;

    // CREATE a new claim for a user
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> createClaim(@PathVariable int userId, @RequestBody Claim claim) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        claim.setUser(userOptional.get());
        Claim savedClaim = claimService.createClaim(claim, userId);

        Map<String, Object> response = new HashMap<>();
        response.put("claimid", savedClaim.getClaimid());
        response.put("descriptioncode", savedClaim.getDescriptioncode());
        response.put("procedurecode", savedClaim.getProcedurecode());
        response.put("price", savedClaim.getPrice());
        response.put("userid", savedClaim.getUser().getUserid());
        response.put("datesubmitted", savedClaim.getDateSubmitted());

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // GET all claims for all users
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllClaims() {
        List<Claim> claims = claimService.getAllClaims();
        if (claims.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Claim c : claims) {
            Map<String, Object> response = new HashMap<>();
            response.put("claimid", c.getClaimid());
            response.put("descriptioncode", c.getDescriptioncode());
            response.put("procedurecode", c.getProcedurecode());
            response.put("price", c.getPrice());
            response.put("userid", c.getUser().getUserid());
            response.put("datesubmitted", c.getDateSubmitted());
            responseList.add(response);
        }
        return ResponseEntity.ok(responseList);
    }

    // GET all claims for a specific user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getClaimsByUser(@PathVariable int userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        List<Claim> claims = claimService.getClaimsByUserId(userId);
        if (claims.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<Map<String, Object>> responseList = new ArrayList<>();
        for (Claim c : claims) {
            Map<String, Object> response = new HashMap<>();
            response.put("claimid", c.getClaimid());
            response.put("descriptioncode", c.getDescriptioncode());
            response.put("procedurecode", c.getProcedurecode());
            response.put("price", c.getPrice());
            response.put("userid", c.getUser().getUserid());
            response.put("datesubmitted", c.getDateSubmitted());
            responseList.add(response);
        }
        return ResponseEntity.ok(responseList);
    }

    // UPDATE a claim
    @PutMapping("/{claimId}")
    public ResponseEntity<?> updateClaim(@PathVariable int claimId, @RequestBody Claim updatedClaim) {
        try {
            Claim claim = claimService.updateClaim(claimId, updatedClaim);
            Map<String, Object> response = new HashMap<>();
            response.put("claimid", claim.getClaimid());
            response.put("descriptioncode", claim.getDescriptioncode());
            response.put("procedurecode", claim.getProcedurecode());
            response.put("price", claim.getPrice());
            response.put("userid", claim.getUser().getUserid());
            response.put("datesubmitted", claim.getDateSubmitted());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Claim not found");
        }
    }

    // DELETE a claim
    @DeleteMapping("/{claimId}")
    public ResponseEntity<?> deleteClaim(@PathVariable int claimId) {
        try {
            claimService.deleteClaim(claimId);
            return ResponseEntity.ok("Claim deleted successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Claim not found");
        }
    }
}
