package com.example.project.dashboard;




import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder; // ایمپورت برای changePassword
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service

public class UserProfileServiceImpl implements UserProfileService  {

    private static final Logger log = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; // برای changePassword لازم است


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found in getCurrentUser for username: {}", username);
                    return new UsernameNotFoundException("کاربر لاگین کرده یافت نشد: " + username);
                });
    }


    private UserProfileDto mapToDto(UserProfile profile, User user) {
        UserProfileDto dto = new UserProfileDto();

        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setCustomerCode(user.getCustomerCode());


        if (profile != null) {
            dto.setFirstName(profile.getFirstName());
            dto.setLastName(profile.getLastName());
            dto.setBirthDate(profile.getBirthDate());
            dto.setNationalId(profile.getNationalId());
            dto.setFatherName(profile.getFatherName());
            dto.setMobilePhone(profile.getMobilePhone());
            dto.setLandlinePhone(profile.getLandlinePhone());
            dto.setProvince(profile.getProvince());
            dto.setCity(profile.getCity());
            dto.setStreet(profile.getStreet());
            dto.setAlley(profile.getAlley());
            dto.setHouseNumber(profile.getHouseNumber());
            dto.setEducationLevel(profile.getEducationLevel());
            dto.setFieldOfStudy(profile.getFieldOfStudy());
            dto.setOccupation(profile.getOccupation());
            dto.setIdCardPhotoPath(profile.getIdCardPhotoPath());
            dto.setNationalIdPhotoPath(profile.getNationalIdPhotoPath());
            dto.setLastUpdatedAt(profile.getLastUpdatedAt());
        }
        return dto;
    }


    @Override
    @Transactional(readOnly = true)
    public UserProfileDto getUserProfile() {
        User currentUser = getCurrentUser();
        UserProfile profile = currentUser.getUserProfile();


        return mapToDto(profile, currentUser);
    }

    @Override
    @Transactional
    public UserProfileDto updateUserProfile(UpdateUserProfileRequest request) {
        User currentUser = getCurrentUser();

        UserProfile profile = currentUser.getUserProfile();

        boolean isNewProfile = false;


        if (profile == null) {

            profile = userProfileRepository.findByUserId(currentUser.getId()).orElse(null);


            if (profile == null) {
                isNewProfile = true;
                log.info("Profile not found for user ID: {}. Creating and associating a new one.", currentUser.getId());
                profile = new UserProfile();
                profile.setId(currentUser.getId());
                profile.setUser(currentUser);
                currentUser.setUserProfile(profile);
            } else {

                log.info("Profile found directly for user ID: {}. Associating with User object.", currentUser.getId());
                currentUser.setUserProfile(profile);
            }
        } else {
            log.info("Profile found via relationship for user ID: {}", currentUser.getId());
        }


        profile.setFirstName(request.getFirstName());
        profile.setLastName(request.getLastName());
        profile.setBirthDate(request.getBirthDate());
        profile.setNationalId(request.getNationalId());
        profile.setFatherName(request.getFatherName());
        profile.setMobilePhone(request.getMobilePhone());
        profile.setLandlinePhone(request.getLandlinePhone());
        profile.setProvince(request.getProvince());
        profile.setCity(request.getCity());
        profile.setStreet(request.getStreet());
        profile.setAlley(request.getAlley());
        profile.setHouseNumber(request.getHouseNumber());
        profile.setEducationLevel(request.getEducationLevel());
        profile.setFieldOfStudy(request.getFieldOfStudy());
        profile.setOccupation(request.getOccupation());

        if (isNewProfile) {
            log.info("New UserProfile populated for user ID: {}. Changes will be persisted on transaction commit.", profile.getId());
        } else {
            log.info("Existing UserProfile updated in memory for user ID: {}. Changes will be persisted on transaction commit.", profile.getId());
        }

        return mapToDto(profile, currentUser);
    }

    @Override
    @Transactional
    public String generateCustomerCode() {
        User currentUser = getCurrentUser();

        if (currentUser.getCustomerCode() != null && !currentUser.getCustomerCode().isEmpty()) {
            log.warn("Attempt to generate customer code for user {} failed: Code already exists.", currentUser.getUsername());
            throw new OperationNotAllowedException("کد مشتری قبلاً برای این کاربر تولید شده است.");
        }

        String generatedCode;
        int randomNum = ThreadLocalRandom.current().nextInt(10000, 100000);
        generatedCode = String.valueOf(randomNum);
        // TODO: (Optional) Add logic here to check if generatedCode already exists in DB and regenerate if needed

        currentUser.setCustomerCode(generatedCode);
        userRepository.save(currentUser);
        log.info("Customer code {} generated for user {}", generatedCode, currentUser.getUsername());
        return generatedCode;
    }


    @Transactional
    public void changePassword(ChangePasswordRequest request) {
        User currentUser = getCurrentUser();

        if (!passwordEncoder.matches(request.getCurrentPassword(), currentUser.getPassword())) {
            log.warn("Password change failed for user {}: Incorrect current password", currentUser.getUsername());
            throw new BadCredentialsException("رمز عبور فعلی نامعتبر است");
        }
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            log.warn("Password change failed for user {}: New passwords do not match", currentUser.getUsername());
            throw new IllegalArgumentException("رمز عبور جدید و تکرار آن مطابقت ندارند");
        }

        currentUser.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(currentUser);
        log.info("Password changed successfully for user {}", currentUser.getUsername());
    }

}