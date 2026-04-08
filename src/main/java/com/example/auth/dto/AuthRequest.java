package com.example.auth.dto;

/**
 * AuthRequest — Data Transfer Object used to receive login/register requests.
 *
 * The client sends a JSON body like:
 * {
 *   "username": "john",
 *   "password": "secret123"
 * }
 *
 * Spring (Jackson) automatically maps that JSON to this Java object.
 * DTOs are separate from the entity to keep API contracts clean.
 */
public class AuthRequest {

    private String username;
    private String password;

    // ===== Default Constructor (required by Jackson for JSON deserialization) =====
    public AuthRequest() {}

    // ===== All-args Constructor =====
    public AuthRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // ===== Getters & Setters =====
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
