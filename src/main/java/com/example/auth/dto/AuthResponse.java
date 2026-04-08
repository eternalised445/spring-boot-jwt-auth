package com.example.auth.dto;

/**
 * AuthResponse — Data Transfer Object sent back to the client after login.
 *
 * The server responds with a JSON body like:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...."
 * }
 *
 * The client stores this token and sends it in the 'Authorization' header
 * for subsequent protected requests.
 */
public class AuthResponse {

    /** The JWT token string that the client will use for authentication. */
    private String token;

    // ===== Default Constructor (required by Jackson) =====
    public AuthResponse() {}

    // ===== All-args Constructor =====
    public AuthResponse(String token) {
        this.token = token;
    }

    // ===== Getter & Setter =====
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}
