package com.example.auth.repository;

import com.example.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — The interface that handles all database operations for User.
 *
 * By extending JpaRepository<User, Long>:
 *   - 'User' is the entity type
 *   - 'Long' is the type of the primary key (id)
 *
 * Spring Data JPA automatically provides methods like:
 *   - save(user)         → INSERT or UPDATE a user
 *   - findById(id)       → SELECT by id
 *   - findAll()          → SELECT all users
 *   - delete(user)       → DELETE a user
 *
 * We can also declare CUSTOM queries by following Spring's method naming convention:
 *   findByUsername(username) → SELECT * FROM users WHERE username = ?
 *   No SQL needed — Spring JPA figures it out from the method name!
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their username.
     * Returns Optional<User> so we can handle the "not found" case without NullPointerException.
     *
     * @param username the username to search for
     * @return an Optional containing the User if found, or empty if not
     */
    Optional<User> findByUsername(String username);
}
