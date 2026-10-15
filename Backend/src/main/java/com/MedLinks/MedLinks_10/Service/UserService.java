package com.MedLinks.MedLinks_10.Service;

import com.MedLinks.MedLinks_10.DTO.RegisterRequest;
import com.MedLinks.MedLinks_10.DTO.UserResponse;

public interface UserService {

    UserResponse register(RegisterRequest request);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);
}