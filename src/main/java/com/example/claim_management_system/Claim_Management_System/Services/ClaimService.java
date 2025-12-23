package com.example.claim_management_system.Claim_Management_System.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.claim_management_system.Claim_Management_System.Objects.Claim;
import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Repositories.ClaimRepository;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimService{

    @Autowired
    private ClaimRepository claimRepository;

    @Autowired
    private UserRepository userRepository;

    public Claim createClaim(Claim claim, int userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        claim.setUser(user);

        if (claim.getDateSubmitted() == null) {
            claim.setDateSubmitted(java.time.LocalDateTime.now());
        }

        return claimRepository.save(claim);
    }

    public Claim updateClaim(int claimId, Claim updatedClaim) {
        Claim existingClaim = claimRepository.findById(claimId)
                .orElseThrow(() -> new RuntimeException("Claim not found"));

        existingClaim.setDescriptioncode(updatedClaim.getDescriptioncode());
        existingClaim.setProcedurecode(updatedClaim.getProcedurecode());
        existingClaim.setPrice(updatedClaim.getPrice());

        return claimRepository.save(existingClaim);
    }

    public void deleteClaim(int claimId) {
        if (!claimRepository.existsById(claimId)) {
            throw new RuntimeException("Claim not found");
        }
        claimRepository.deleteById(claimId);
    }

    public Optional<Claim> getClaimById(int claimId) {
        return claimRepository.findById(claimId);
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAll();
    }

    public List<Claim> getClaimsByUserId(int userId) {
        return claimRepository.findByUser_Userid(userId);
    }
}
