package com.example.project.dashboard;


public interface UserProfileService {


    UserProfileDto getUserProfile();


    UserProfileDto updateUserProfile(UpdateUserProfileRequest request);
    String generateCustomerCode();

}