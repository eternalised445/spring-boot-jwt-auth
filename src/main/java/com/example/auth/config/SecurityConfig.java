package com.example.auth.config;

import com.example.auth.security.JwtAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * SecurityConfig — Configures Spring Security for our application.
 *
 * Key decisions here:
 *   - Which endpoints are PUBLIC (no token needed)?
 *   - Which endpoints are PROTECTED (token required)?
 *   - How are sessions managed? (We use STATELESS — no server-side sessions)
 *   - Where does our JWT filter plug in?
 *
 * @Configuration : This is a Spring configuration class (like a settings file).
 * @EnableWebSecurity : Activates Spring Security's web security support.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * Our custom JWT filter that checks tokens on each request.
     * Spring will inject this automatically because it's a @Component.
     */
    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    /**
     * SecurityFilterChain — The main security configuration bean.
     *
     * This defines the rules for which requests are allowed and how.
     *
     * @param http the HttpSecurity object provided by Spring
     * @return a configured SecurityFilterChain
     * @throws Exception if configuration fails
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // ---- Disable CSRF (Cross-Site Request Forgery) ----
            // CSRF protection is for browser-based sessions. Since we use JWT (stateless),
            // we don't need it. REST APIs typically disable CSRF.
            .csrf(csrf -> csrf.disable())

            // ---- Define which endpoints require authentication ----
            .authorizeHttpRequests(auth -> auth
                // These endpoints are PUBLIC — anyone can access them (no token needed)
                .requestMatchers("/auth/register", "/auth/login").permitAll()
                // Everything else REQUIRES a valid JWT token
                .anyRequest().authenticated()
            )

            // ---- Session Management: STATELESS ----
            // We don't store sessions on the server — the JWT token IS the session.
            // Each request must carry its own token.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // ---- Add JWT Filter ----
            // Our JwtAuthFilter runs BEFORE Spring's default UsernamePasswordAuthenticationFilter.
            // This way, JWT authentication is checked first on every request.
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * PasswordEncoder Bean — Tells Spring how to hash and verify passwords.
     *
     * BCrypt is a strong, adaptive hashing function perfect for passwords.
     * It automatically adds a random "salt" to prevent rainbow table attacks.
     * Strength factor 12 means it performs 2^12 = 4096 hashing rounds.
     *
     * @return a BCryptPasswordEncoder instance
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
