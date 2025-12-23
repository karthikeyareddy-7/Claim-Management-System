package com.example.claim_management_system.Claim_Management_System.Repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.claim_management_system.Claim_Management_System.Objects.Claim;

public interface ClaimRepository extends JpaRepository<Claim,Integer> {

	List<Claim> findByUser_Userid(int userId);

}
