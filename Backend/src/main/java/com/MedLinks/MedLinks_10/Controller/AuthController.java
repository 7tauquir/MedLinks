package com.MedLinks.MedLinks_10.Controller;

import com.MedLinks.MedLinks_10.DTO.LoginRequest;
import com.MedLinks.MedLinks_10.DTO.LoginResponse;
import com.MedLinks.MedLinks_10.DTO.RegisterRequest;
import com.MedLinks.MedLinks_10.DTO.UserResponse;
import com.MedLinks.MedLinks_10.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(
            AuthService authService
    ) {
        this.authService = authService;
    }

    // ==============================
    // REGISTER
    // ==============================

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(
            @Valid @RequestBody RegisterRequest request
    ) {

        UserResponse response =
                authService.register(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==============================
    // LOGIN
    // ==============================

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        LoginResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}