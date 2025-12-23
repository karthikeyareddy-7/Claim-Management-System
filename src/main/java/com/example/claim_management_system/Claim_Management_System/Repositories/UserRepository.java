package com.example.claim_management_system.Claim_Management_System.Repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.claim_management_system.Claim_Management_System.Objects.User;

public interface UserRepository extends JpaRepository<User,Integer>{

	Optional<User> findByUsername(String username);

	Optional<User> findByUsernameAndPassword(String username, String password);

	Optional<User> findById(Integer id);

	Optional<User> findByEmail(String email);

	Optional<User> getUserByUsername(String username);

}
