package com.example.auth.service;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService — Contains the BUSINESS LOGIC for registration and login.
 *
 * The service layer is where the actual application logic lives.
 * Controllers receive requests and delegate to services.
 * Services perform the work and return results.
 *
 * @Service : Marks this as a Spring-managed service bean.
 */
@Service
public class AuthService {

    /**
     * @Autowired lets Spring automatically inject the required objects.
     * Spring creates these objects (beans) and "wires" them together.
     */
    @Autowired
    private UserRepository userRepository; // For database operations

    @Autowired
    private JwtUtil jwtUtil;               // For creating JWT tokens

    @Autowired
    private PasswordEncoder passwordEncoder; // For hashing passwords

    /**
     * register() — Handles new user sign-up.
     *
     * Steps:
     *   1. Check if username is already taken
     *   2. Hash the password using BCrypt
     *   3. Save the new user to the database
     *   4. Return a success message
     *
     * @param request contains username and plain-text password from the client
     * @return a success message string
     * @throws RuntimeException if the username is already in use
     */
    public String register(AuthRequest request) {

        // Step 1: Check if a user with this username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            // Throw an exception — the controller will catch this and return 400 Bad Request
            throw new RuntimeException("Username already exists: " + request.getUsername());
        }

        // Step 2: Create a new User entity and populate its fields
        User newUser = new User();
        newUser.setUsername(request.getUsername());

        // NEVER store plain-text passwords!
        // BCrypt transforms "mypassword" into something like "$2a$12$5K8b..."
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Assign a default role of "USER" for all registered users
        newUser.setRole("USER");

        // Step 3: Save the user to MySQL via JPA
        userRepository.save(newUser);

        // Step 4: Return a confirmation message
        return "User registered successfully: " + request.getUsername();
    }

    /**
     * login() — Handles user authentication and JWT token issuance.
     *
     * Steps:
     *   1. Find the user by username in the database
     *   2. Verify the provided password matches the stored hashed password
     *   3. Generate a JWT token for the authenticated user
     *   4. Return the token in an AuthResponse DTO
     *
     * @param request contains username and plain-text password from the client
     * @return AuthResponse containing the JWT token
     * @throws RuntimeException if credentials are invalid
     */
    public AuthResponse login(AuthRequest request) {

        // Step 1: Look up the user by username
        // orElseThrow() throws an exception if no user is found (404/403 scenario)
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        // Step 2: Compare the raw password (from request) with the hashed password (from DB)
        // BCrypt's matches() method handles the comparison safely
        boolean passwordMatches = passwordEncoder.matches(request.getPassword(), user.getPassword());

        if (!passwordMatches) {
            // Intentionally vague message — don't reveal whether username or password is wrong
            throw new RuntimeException("Invalid credentials");
        }

        // Step 3: Generate a JWT token using the username as the subject
        String token = jwtUtil.generateToken(user.getUsername());

        // Step 4: Wrap the token in a response DTO and return it
        return new AuthResponse(token);
    }
}
