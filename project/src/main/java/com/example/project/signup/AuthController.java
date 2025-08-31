package com.example.project.signup; // یا پکیج صحیح پروژه شما

 // DTO پاسخ ثبت نام (اختیاری)

import jakarta.validation.Valid;
import org.slf4j.Logger;                         // برای لاگ گرفتن
import org.slf4j.LoggerFactory;                  // برای لاگ گرفتن
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // Exception برای رمز/نام کاربری اشتباه
import org.springframework.web.bind.annotation.ExceptionHandler; // برای مدیریت متمرکز Exception ها (اختیاری)
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
// import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService; // سرویس احراز هویت

    // --- اندپوینت ثبت نام (از کد شما) ---
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        log.info("Received registration request for username: {}", registerRequest.getUsername());



        try {
            User registeredUser = authService.registerUser(registerRequest);
            log.info("User registered successfully: {}", registeredUser.getUsername());


            return ResponseEntity.status(HttpStatus.CREATED).body("کاربر با موفقیت ثبت نام شد!");

        } catch (UserAlreadyExistsException e) {
            log.warn("Registration failed: {}", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {

            log.error("Unexpected error during registration for user: {}", registerRequest.getUsername(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطای داخلی سرور در زمان ثبت نام");
        }

    }


    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        log.info("Received login request for username: {}", loginRequest.getUsername());
        try {

            JwtResponse jwtResponse = authService.loginUser(loginRequest);
            log.info("User authenticated successfully: {}", loginRequest.getUsername());

            return ResponseEntity.ok(jwtResponse);

        } catch (BadCredentialsException e) {

            log.warn("Authentication failed for user {}: Invalid credentials", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("نام کاربری یا رمز عبور نامعتبر است");
        } catch (Exception e) {

            log.error("Unexpected error during login for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطای داخلی هنگام ورود");
        }
    }

}