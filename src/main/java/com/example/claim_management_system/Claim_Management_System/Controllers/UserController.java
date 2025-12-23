package com.example.claim_management_system.Claim_Management_System.Controllers;

import com.example.claim_management_system.Claim_Management_System.Objects.User;
import com.example.claim_management_system.Claim_Management_System.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:4200") // allow Angular frontend
public class UserController {

	@Autowired
	private UserRepository userRepository;

	// REGISTER USER
	@PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> registerUser(@RequestBody User user) {
		Optional<User> existing = userRepository.findByUsername(user.getUsername());
		if (existing.isPresent()) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("Username already exists");
		}
		userRepository.save(user);
		return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully!");
	}

	// LOGIN USER
	@PostMapping(value = "/login", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.TEXT_PLAIN_VALUE)
	public ResponseEntity<String> loginUser(@RequestBody User loginRequest) {
		Optional<User> optionalUser = userRepository.findByUsername(loginRequest.getUsername());

		if (optionalUser.isPresent()) {
			User user = optionalUser.get();
			if (user.getPassword().equals(loginRequest.getPassword())) {
				return ResponseEntity.ok("Login successful");
			} else {
				return ResponseEntity.status(401).body("Invalid password");
			}
		} else {
			return ResponseEntity.status(404).body("User not found");
		}
	}

	@GetMapping("/{username}")
	public ResponseEntity<User> getUserByUsername(@PathVariable String username) {
		return userRepository.getUserByUsername(username).map(ResponseEntity::ok)
				.orElse(ResponseEntity.notFound().build());
	}
}
