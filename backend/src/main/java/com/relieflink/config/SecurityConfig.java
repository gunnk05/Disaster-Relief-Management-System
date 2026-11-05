package com.relieflink.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration.
 *
 * <p>Implements stateless JWT-based authentication with role-based
 * authorization rules matching the original Node.js middleware.</p>
 *
 * <p><b>Open/Closed Principle:</b> New role rules can be added to
 * {@link #securityFilterChain} without modifying existing rules.
 * Method-level security is enabled via {@code @EnableMethodSecurity}
 * for finer-grained control in controllers.</p>
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .authorizeHttpRequests(auth -> auth
                // Public endpoints
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/health").permitAll()

                // Admin-only endpoints
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/resources", "/resources/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/resources/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/resources/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/shelters", "/shelters/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/shelters/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/shelters/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/locations", "/locations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/locations/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/locations/**").hasRole("ADMIN")
                .requestMatchers("/users").hasRole("ADMIN")

                // Volunteer endpoints
                .requestMatchers("/volunteers/**").hasRole("VOLUNTEER")

                // Citizen endpoints
                .requestMatchers(HttpMethod.POST, "/requests").hasRole("CITIZEN")
                .requestMatchers("/requests/my").hasRole("CITIZEN")

                // Read-only endpoints — any authenticated user
                .requestMatchers(HttpMethod.GET, "/resources", "/resources/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/shelters", "/shelters/**").authenticated()
                .requestMatchers(HttpMethod.GET, "/locations", "/locations/**").authenticated()

                // All other endpoints require authentication
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
}
