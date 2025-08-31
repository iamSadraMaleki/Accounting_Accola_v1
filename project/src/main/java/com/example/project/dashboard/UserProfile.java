package com.example.project.dashboard; // <<< پکیج صحیح مدل شما

import com.example.project.signup.User; // <<< مطمئن شوید ایمپورت User از پکیج درست است
import jakarta.persistence.*;
import lombok.Data;
// import lombok.NoArgsConstructor; // بودنش ضرری ندارد ولی چون دستی مینویسیم لازم نیست حتما باشه
import org.hibernate.annotations.UpdateTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_profiles")
@Data

public class UserProfile {

    @Id

    private Long id;


    private String firstName;
    private String lastName;
    private LocalDate birthDate;
    @Column(unique = true, length = 10)
    private String nationalId;
    private String fatherName;


    @Column(length = 15)
    private String mobilePhone;
    @Column(length = 15)
    private String landlinePhone;
    private String province;
    private String city;
    private String street;
    private String alley;
    private String houseNumber;


    private String educationLevel;
    private String fieldOfStudy;
    private String occupation;


    private String idCardPhotoPath;
    private String nationalIdPhotoPath;


    @UpdateTimestamp
    private LocalDateTime lastUpdatedAt;


    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;


    public UserProfile() {

    }

    public UserProfile(User user) {
        this.user = user;
        if (user != null) {
            this.id = user.getId();
        }
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getNationalId() {
        return nationalId;
    }

    public void setNationalId(String nationalId) {
        this.nationalId = nationalId;
    }

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public String getLandlinePhone() {
        return landlinePhone;
    }

    public void setLandlinePhone(String landlinePhone) {
        this.landlinePhone = landlinePhone;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getAlley() {
        return alley;
    }

    public void setAlley(String alley) {
        this.alley = alley;
    }

    public String getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(String houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getEducationLevel() {
        return educationLevel;
    }

    public void setEducationLevel(String educationLevel) {
        this.educationLevel = educationLevel;
    }

    public String getFieldOfStudy() {
        return fieldOfStudy;
    }

    public void setFieldOfStudy(String fieldOfStudy) {
        this.fieldOfStudy = fieldOfStudy;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }

    public String getIdCardPhotoPath() {
        return idCardPhotoPath;
    }

    public void setIdCardPhotoPath(String idCardPhotoPath) {
        this.idCardPhotoPath = idCardPhotoPath;
    }

    public String getNationalIdPhotoPath() {
        return nationalIdPhotoPath;
    }

    public void setNationalIdPhotoPath(String nationalIdPhotoPath) {
        this.nationalIdPhotoPath = nationalIdPhotoPath;
    }

    public LocalDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}