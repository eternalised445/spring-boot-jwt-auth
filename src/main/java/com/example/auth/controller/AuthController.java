package com.example.auth.controller;

import com.example.auth.dto.AuthRequest;
import com.example.auth.dto.AuthResponse;
import com.example.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — Handles all incoming HTTP requests for authentication.
 *
 * The controller layer is the "entry point" for HTTP requests.
 * It receives requests, delegates work to the service, and returns responses.
 *
 * @RestController : Combines @Controller + @ResponseBody.
 *   - @Controller    : marks this as a Spring MVC controller
 *   - @ResponseBody  : automatically serializes return values to JSON
 *
 * @RequestMapping("/auth") : All endpoints in this class start with /auth
 *   - So our endpoints are: /auth/register, /auth/login, /auth/test
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    /**
     * Spring injects the AuthService bean automatically via @Autowired.
     * The controller uses the service for all business logic.
     */
    @Autowired
    private AuthService authService;

    // =========================================================
    //  POST /auth/register
    //  PUBLIC — No token required
    // =========================================================

    /**
     * register() — Registers a new user account.
     *
     * Request Body (JSON):
     * {
     *   "username": "john",
     *   "password": "secret123"
     * }
     *
     * Success Response (201 Created):
     * "User registered successfully: john"
     *
     * Failure Response (400 Bad Request):
     * "Username already exists: john"
     *
     * @param request the JSON body auto-mapped to AuthRequest
     * @return ResponseEntity with the result message and HTTP status
     */
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        try {
            // Delegate to service layer for actual registration logic
            String message = authService.register(request);
            // 201 CREATED is the correct status for resource creation
            return ResponseEntity.status(HttpStatus.CREATED).body(message);
        } catch (RuntimeException e) {
            // If registration fails (e.g., duplicate username), return 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // =========================================================
    //  POST /auth/login
    //  PUBLIC — No token required
    // =========================================================

    /**
     * login() — Authenticates a user and returns a JWT token.
     *
     * Request Body (JSON):
     * {
     *   "username": "john",
     *   "password": "secret123"
     * }
     *
     * Success Response (200 OK):
     * {
     *   "token": "eyJhbGci..."
     * }
     *
     * Failure Response (401 Unauthorized):
     * "Invalid credentials"
     *
     * @param request the JSON body auto-mapped to AuthRequest
     * @return ResponseEntity with AuthResponse (containing token) or error message
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Delegate to service; returns AuthResponse with JWT token
            AuthResponse response = authService.login(request);
            // 200 OK — successful authentication
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // 401 Unauthorized — wrong username or password
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    // =========================================================
    //  GET /auth/test
    //  PROTECTED — Valid JWT token required in Authorization header
    // =========================================================

    /**
     * test() — A protected endpoint to verify JWT authentication is working.
     *
     * To call this endpoint, the client MUST include:
     *   Header: Authorization: Bearer <your-token-here>
     *
     * If no token or invalid token → Spring Security returns 403 Forbidden automatically.
     *
     * Success Response (200 OK):
     * "✅ Hello john! You've accessed a protected endpoint."
     *
     * @return a welcome message showing the authenticated user's name
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        // SecurityContextHolder stores info about the currently authenticated user
        // We set this in JwtAuthFilter after successfully validating the token
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // The "principal" name is the username we stored when authenticating in JwtAuthFilter
        String username = authentication.getName();

        return ResponseEntity.ok("✅ Hello " + username + "! You've accessed a protected endpoint. Your role: "
                + authentication.getAuthorities());
    }
}
