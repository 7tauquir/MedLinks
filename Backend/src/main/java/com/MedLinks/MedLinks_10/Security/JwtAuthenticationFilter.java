package com.MedLinks.MedLinks_10.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            CustomUserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        System.out.println("===== REQUEST =====");
        System.out.println(
                request.getMethod() + " " + request.getRequestURI()
        );
        System.out.println(
                "AUTH HEADER: " + request.getHeader("Authorization")
        );

        String authHeader = request.getHeader("Authorization");

        String token = null;
        String email = null;

        // ==========================================
        // READ JWT TOKEN
        // ==========================================

        if (authHeader != null &&
                authHeader.startsWith("Bearer ")) {

            token = authHeader.substring(7);

            try {
                email = jwtService.extractEmail(token);

                System.out.println("=== JWT FILTER ===");
                System.out.println("Request: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI());
                System.out.println("JWT Email: " + email);

            } catch (Exception e) {

                System.out.println(
                        "JWT ERROR: " + e.getMessage()
                );
            }
        }

        // ==========================================
        // AUTHENTICATE USER
        // ==========================================

        if (email != null &&
                SecurityContextHolder
                        .getContext()
                        .getAuthentication() == null) {

            UserDetails userDetails =
                    userDetailsService
                            .loadUserByUsername(email);

            if (jwtService.isTokenValid(
                    token,
                    userDetails
            )) {

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource()
                                .buildDetails(request)
                );

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(
                                authentication
                        );

                System.out.println(
                        "Authenticated User: "
                                + userDetails.getUsername()
                );

                System.out.println(
                        "Authorities: "
                                + userDetails.getAuthorities()
                );
            }
        }

        // ==========================================
        // CONTINUE REQUEST
        // ==========================================

        System.out.println("===== SECURITY CONTEXT =====");
        System.out.println(
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
        );

        filterChain.doFilter(
                request,
                response
        );
    }
}