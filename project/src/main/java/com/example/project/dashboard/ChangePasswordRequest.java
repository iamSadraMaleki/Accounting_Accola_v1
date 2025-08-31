package com.example.project.dashboard;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePasswordRequest {

    @NotBlank(message = "رمز عبور فعلی نمی‌تواند خالی باشد")
    private String currentPassword;

    @NotBlank(message = "رمز عبور جدید نمی‌تواند خالی باشد")
    @Size(min = 6, message = "رمز عبور جدید باید حداقل 6 کاراکتر باشد")
    private String newPassword;

    @NotBlank(message = "تایید رمز عبور جدید نمی‌تواند خالی باشد")
    private String confirmNewPassword;

    public String getCurrentPassword() {
        return currentPassword;
    }

    public void setCurrentPassword(String currentPassword) {
        this.currentPassword = currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}