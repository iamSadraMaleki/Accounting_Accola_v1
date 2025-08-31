package com.example.project.signup;

import com.example.project.cardmanagment.BankCard;
import com.example.project.dashboard.UserProfile;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email"),
        @UniqueConstraint(columnNames = "customerCode")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "نام کاربری نمی‌تواند خالی باشد")
    @Size(min = 3, max = 50, message = "نام کاربری باید بین 3 تا 50 کاراکتر باشد")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "رمز عبور نمی‌تواند خالی باشد")
    @Size(min = 6, message = "رمز عبور باید حداقل 6 کاراکتر باشد")
    @Column(nullable = false)
    private String password;

    @NotBlank(message = "ایمیل نمی‌تواند خالی باشد")
    @Email(message = "فرمت ایمیل نامعتبر است")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 5, unique = true)
    private String customerCode;

    private String fullName;

    private String role = "USER";

    @Size(max = 150)
    private String businessName;

    @Size(max = 50)
    private String guildCode;

    @Size(max = 100)
    private String unionType;

    @Size(max = 100)
    private String activityType;

    @Size(max = 50)
    private String businessUnitGrade;

    @Size(max = 500)
    private String workAddress;

    @Size(max = 20)
    private String workPhone;

    @Size(max = 500)
    private String businessLicensePath;

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    public String getGuildCode() {
        return guildCode;
    }

    public void setGuildCode(String guildCode) {
        this.guildCode = guildCode;
    }

    public String getUnionType() {
        return unionType;
    }

    public void setUnionType(String unionType) {
        this.unionType = unionType;
    }

    public String getActivityType() {
        return activityType;
    }

    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getBusinessUnitGrade() {
        return businessUnitGrade;
    }

    public void setBusinessUnitGrade(String businessUnitGrade) {
        this.businessUnitGrade = businessUnitGrade;
    }

    public String getWorkAddress() {
        return workAddress;
    }

    public void setWorkAddress(String workAddress) {
        this.workAddress = workAddress;
    }

    public String getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(String workPhone) {
        this.workPhone = workPhone;
    }

    public String getBusinessLicensePath() {
        return businessLicensePath;
    }

    public void setBusinessLicensePath(String businessLicensePath) {
        this.businessLicensePath = businessLicensePath;
    }

    @CreationTimestamp // تاریخ و زمان ایجاد رکورد به صورت خودکار ثبت می‌شود
    @Column(updatable = false) // در زمان آپدیت تغییر نکند
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserProfile userProfile;

    public UserProfile getUserProfile() {
        return this.userProfile;
    }

    // --- یک متد کمکی برای تنظیم دوطرفه ارتباط (اختیاری ولی خوب) ---
    public void setUserProfile(UserProfile userProfile) {
        if (userProfile == null) {
            if (this.userProfile != null) {
                this.userProfile.setUser(null); // ارتباط از سمت پروفایل هم قطع شود
            }
        } else {
            userProfile.setUser(this); // ارتباط از سمت پروفایل ست شود
        }
        this.userProfile = userProfile;
    }

    public String getCustomerCode() {
        return customerCode;
    }

    public void setCustomerCode(String customerCode) {
        this.customerCode = customerCode;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BankCard> bankCards = new ArrayList<>();

    public List<BankCard> getBankCards() {
        return bankCards;
    }

    public void setBankCards(List<BankCard> bankCards) {
        this.bankCards = bankCards;
    }
}