package com.example.project.dashboard;


import com.example.project.signup.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // برای خطای رمز فعلی
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/profile")

public class UserProfileController {

    private static final Logger log = LoggerFactory.getLogger(UserProfileController.class);

    @Autowired
    private UserProfileService userProfileService;

    @Autowired
    private AuthService authService;


    @GetMapping("/me")
    public ResponseEntity<UserProfileDto> getCurrentUserProfile() {
        log.info("Request received for getting current user profile");
        UserProfileDto userProfileDto = userProfileService.getUserProfile();
        return ResponseEntity.ok(userProfileDto);
    }


    @PutMapping("/me")
    public ResponseEntity<UserProfileDto> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest profileRequest) {
        log.info("Request received to update user profile");
        try {
            UserProfileDto updatedProfile = userProfileService.updateUserProfile(profileRequest);
            log.info("User profile updated successfully");
            return ResponseEntity.ok(updatedProfile);
        } catch (Exception e) {

            log.error("Error updating user profile", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/me/change-password")
    public ResponseEntity<?> changeCurrentUserPassword(@Valid @RequestBody ChangePasswordRequest passwordRequest) {
        log.info("Request received to change password");
        try {
            authService.changePassword(passwordRequest);
            log.info("Password changed successfully for current user");
            return ResponseEntity.ok("رمز عبور با موفقیت تغییر کرد.");
        } catch (BadCredentialsException e) {

            log.warn("Password change failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (IllegalArgumentException e) {

            log.warn("Password change failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error changing password", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطای داخلی هنگام تغییر رمز عبور.");
        }
    }
    @PostMapping("/me/customer-code")
    public ResponseEntity<?> generateCustomerCodeForCurrentUser() {
        log.info("Request received to generate customer code for current user");
        try {
            String customerCode = userProfileService.generateCustomerCode();
            // فقط خود کد رو در پاسخ برمی‌گردونیم
            return ResponseEntity.ok(customerCode);
        } catch (OperationNotAllowedException e) {
            // اگر کد از قبل وجود داشته
            log.warn("Customer code generation failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        } catch (Exception e) {
            log.error("Error generating customer code", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("خطای داخلی هنگام تولید کد مشتری.");
        }
    }

}
