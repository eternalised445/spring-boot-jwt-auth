package com.example.auth.security;

import com.example.auth.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JwtAuthFilter — A custom filter that runs ONCE per HTTP request.
 *
 * Its job:
 *   1. Look for a JWT token in the 'Authorization' request header
 *   2. If found, validate the token
 *   3. If valid, tell Spring Security "this user is authenticated"
 *
 * Extends OncePerRequestFilter to guarantee it runs exactly once per request.
 * Spring Security processes a chain of filters — this is one of them.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    /**
     * This method is called for every incoming HTTP request.
     *
     * Flow:
     *   Request → JwtAuthFilter → (if valid token) authenticate → Controller
     *
     * @param request     the incoming HTTP request
     * @param response    the outgoing HTTP response
     * @param filterChain pass control to the next filter in the chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // Step 1: Read the 'Authorization' header from the request
        // Expected format: "Bearer eyJhbGci..."
        String authHeader = request.getHeader("Authorization");

        // Step 2: If there's no Authorization header, skip JWT processing
        // The request will continue — SecurityConfig decides if it's allowed or not
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: Extract the token by removing the "Bearer " prefix (7 characters)
        String token = authHeader.substring(7);

        // Step 4: Extract the username from the token's "subject" claim
        String username = jwtUtil.extractUsername(token);

        // Step 5: Validate the token and set authentication in Spring Security context
        // SecurityContextHolder holds the authentication state for the current request thread
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Check the user actually exists in the database
            userRepository.findByUsername(username).ifPresent(user -> {

                // Validate token is not expired and belongs to this user
                if (jwtUtil.validateToken(token, username)) {

                    // Create an Authentication object that Spring Security understands
                    // This says: "user with this username and role is authenticated"
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    username,
                                    null, // Credentials (password) — not needed after token validation
                                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole()))
                            );

                    // Store authentication in the context — this marks the user as logged in
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            });
        }

        // Step 6: Pass the request on to the next filter (or the controller)
        filterChain.doFilter(request, response);
    }
}
