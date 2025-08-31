package com.example.project.signup;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "نام کاربری نمی‌تواند خالی باشد")
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
    @Size(min = 6)
    private String password;

    @NotBlank(message = "ایمیل نمی‌تواند خالی باشد")
    @Email
    private String email;

    private String fullName;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
