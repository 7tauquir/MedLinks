package com.MedLinks.MedLinks_10.Service;

import com.MedLinks.MedLinks_10.DTO.LoginRequest;
import com.MedLinks.MedLinks_10.DTO.LoginResponse;
import com.MedLinks.MedLinks_10.DTO.RegisterRequest;
import com.MedLinks.MedLinks_10.DTO.UserResponse;

public interface AuthService {

    UserResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);
}