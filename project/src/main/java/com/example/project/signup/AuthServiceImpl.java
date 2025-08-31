package com.example.project.signup;


import com.example.project.dashboard.ChangePasswordRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service

public class AuthServiceImpl implements AuthService {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public User registerUser(RegisterRequest registerRequest) {

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new UserAlreadyExistsException("کاربری با این نام کاربری از قبل وجود دارد: " + registerRequest.getUsername());
        }


        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new UserAlreadyExistsException("کاربری با این ایمیل از قبل وجود دارد: " + registerRequest.getEmail());
        }


        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setFullName(registerRequest.getFullName());


        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));


        newUser.setRole("USER"); // یا هر نقش پیش‌فرض دیگر


        return userRepository.save(newUser);
    }


    @Override
    public JwtResponse loginUser(LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));


        SecurityContextHolder.getContext().setAuthentication(authentication);


        String jwt = jwtUtils.generateJwtToken(authentication);


        User user = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow(
                () -> new RuntimeException("خطای داخلی: کاربر بعد از احراز هویت یافت نشد!") // این خطا نباید رخ دهد
        );


        return new JwtResponse(jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole());

    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();


        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            throw new BadCredentialsException("رمز عبور فعلی نامعتبر است");
        }


        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new IllegalArgumentException("رمز عبور جدید و تکرار آن مطابقت ندارند"); // یا Exception سفارشی
        }


        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));


        userRepository.save(currentUser);
    }


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر لاگین کرده یافت نشد: " + username));
    }


}