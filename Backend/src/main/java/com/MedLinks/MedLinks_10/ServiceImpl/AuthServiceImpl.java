package com.MedLinks.MedLinks_10.ServiceImpl;

import com.MedLinks.MedLinks_10.DTO.LoginRequest;
import com.MedLinks.MedLinks_10.DTO.LoginResponse;
import com.MedLinks.MedLinks_10.DTO.RegisterRequest;
import com.MedLinks.MedLinks_10.DTO.UserResponse;
import com.MedLinks.MedLinks_10.Entities.User;
import com.MedLinks.MedLinks_10.Repository.UserRepository;
import com.MedLinks.MedLinks_10.Security.JwtService;
import com.MedLinks.MedLinks_10.Service.AuthService;
import com.MedLinks.MedLinks_10.Service.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthServiceImpl(
            UserService userService,
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    @Override
    public UserResponse register(
            RegisterRequest request
    ) {
        return userService.register(request);
    }

    @Override
    public LoginResponse login(
            LoginRequest request
    ) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new RuntimeException(
                                "User not found"
                        )
                );

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name()
        );

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                "Login successful"
        );
    }
}