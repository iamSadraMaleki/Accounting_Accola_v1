package com.example.project.signup;

import com.example.project.dashboard.ChangePasswordRequest;

public interface AuthService {
    User registerUser(RegisterRequest registerRequest);
    JwtResponse loginUser(LoginRequest loginRequest);
    void changePassword(ChangePasswordRequest request);
}
