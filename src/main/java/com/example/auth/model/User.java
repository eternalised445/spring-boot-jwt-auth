package com.example.auth.model;

import jakarta.persistence.*;

/**
 * User — The JPA Entity that maps to the 'users' table in MySQL.
 *
 * @Entity   : Tells JPA this class is a database table.
 * @Table    : Specifies the exact table name to use.
 */
@Entity
@Table(name = "users")
public class User {

    /** @Id marks this field as the Primary Key. */
    @Id
    /** AUTO_INCREMENT equivalent — database generates the ID automatically. */
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @Column(unique = true) ensures no two users share the same username.
     * nullable = false means the column cannot be empty.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /** Stores the BCrypt-hashed password (never store plain text passwords!). */
    @Column(nullable = false)
    private String password;

    /**
     * Role of the user — e.g., "USER" or "ADMIN".
     * We keep this simple (just a String), no complex RBAC needed.
     */
    @Column(nullable = false)
    private String role;

    // ===== Default (no-arg) Constructor =====
    // JPA requires a no-argument constructor to instantiate entities from DB rows.
    public User() {}

    // ===== All-args Constructor =====
    public User(Long id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    // ===== Getters & Setters =====
    // These let other classes read and write the private fields.

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
