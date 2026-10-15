package com.MedLinks.MedLinks_10.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(
            JwtAuthenticationFilter jwtAuthenticationFilter
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration
    ) throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // ==========================================
                        // PUBLIC AUTH APIs
                        // ==========================================

                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()


                        // ==========================================
                        // MEDICINE APIs
                        // ==========================================

                        // Only wholesaler can see his own medicines
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medicines/seller"
                        )
                        .hasRole("WHOLESALER")

                        // Anyone authenticated can browse medicines
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/medicines/**"
                        )
                        .authenticated()

                        // Only wholesaler can add medicine
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/medicines/**"
                        )
                        .hasRole("WHOLESALER")

                        // Only wholesaler can update medicine
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/medicines/**"
                        )
                        .hasRole("WHOLESALER")

                        // Only wholesaler can delete medicine
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/medicines/**"
                        )
                        .hasRole("WHOLESALER")


                        // ==========================================
                        // WHOLESALER APIs
                        // ==========================================

                        .requestMatchers(
                                "/api/wholesaler/**"
                        )
                        .hasRole("WHOLESALER")


                        // ==========================================
                        // CART APIs
                        // ==========================================

                        .requestMatchers(
                                "/api/cart/**"
                        )
                        .hasRole("SHOP_OWNER")


                        // ==========================================
                        // SHOP OWNER APIs
                        // ==========================================

                        .requestMatchers(
                                "/api/shop-owner/**"
                        )
                        .hasRole("SHOP_OWNER")


                        // ==========================================
                        // ORDER APIs
                        // ==========================================

                        // Wholesaler:
                        // View orders received by wholesaler
                        // Update order status
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/wholesaler"
                        )
                        .hasRole("WHOLESALER")

                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orders/*/status"
                        )
                        .hasRole("WHOLESALER")


                        // Shop Owner:
                        // Place order
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/orders"
                        )
                        .hasRole("SHOP_OWNER")

                        // Shop owner's own orders
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/my"
                        )
                        .hasRole("SHOP_OWNER")

                        // Cancel own order
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/orders/*/cancel"
                        )
                        .hasRole("SHOP_OWNER")


                        // Get individual order
                        // Both authenticated roles can access,
                        // Service layer will verify ownership.
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/orders/*"
                        )
                        .authenticated()


                        // ==========================================
                        // ADMIN APIs
                        // ==========================================

                        .requestMatchers(
                                "/api/admin/**"
                        )
                        .hasRole("ADMIN")


                        // ==========================================
                        // EVERYTHING ELSE
                        // ==========================================

                        .anyRequest()
                        .authenticated()
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}