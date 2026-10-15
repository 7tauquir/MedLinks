package com.MedLinks.MedLinks_10.ServiceImpl;

import com.MedLinks.MedLinks_10.DTO.RegisterRequest;
import com.MedLinks.MedLinks_10.DTO.UserResponse;
import com.MedLinks.MedLinks_10.Entities.Role;
import com.MedLinks.MedLinks_10.Entities.User;
import com.MedLinks.MedLinks_10.Repository.UserRepository;
import com.MedLinks.MedLinks_10.Service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserResponse register(RegisterRequest request) {

        // Check existing email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        // Create User
        User user = new User();

        user.setName(request.getName());
        user.setEmail(request.getEmail());

        // Encrypt password
        user.setPassword(
                passwordEncoder.encode(request.getPassword())
        );

        user.setPhone(request.getPhone());
        user.setBusinessName(request.getBusinessName());
        user.setAddress(request.getAddress());

        /*
         * MedLinks V1:
         *
         * One Wholesaler
         * Many Shop Owners
         *
         * Admin can also be created later.
         */
        if (request.getRole() == null) {
            throw new RuntimeException("Role is required");
        }

        if (request.getRole() == Role.WHOLESALER) {

            boolean wholesalerExists = userRepository
                    .findAll()
                    .stream()
                    .anyMatch(User -> user.getRole() == Role.WHOLESALER);

            if (wholesalerExists) {
                throw new RuntimeException(
                        "Only one wholesaler is allowed in MedLinks V1"
                );
            }
        }

        user.setRole(request.getRole());

        User savedUser = userRepository.save(user);

        return convertToResponse(savedUser);
    }

    @Override
    public UserResponse getUserById(Long id) {

        User user = userRepository.findById(id)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        return convertToResponse(user);
    }

    @Override
    public UserResponse getUserByEmail(String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(
                        () -> new RuntimeException("User not found")
                );

        return convertToResponse(user);
    }

    private UserResponse convertToResponse(User user) {

        UserResponse response = new UserResponse();

        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setBusinessName(user.getBusinessName());
        response.setAddress(user.getAddress());
        response.setRole(user.getRole());

        return response;
    }
}