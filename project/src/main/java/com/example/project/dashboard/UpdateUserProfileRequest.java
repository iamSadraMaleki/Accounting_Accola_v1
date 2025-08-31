package com.example.project.dashboard;



import jakarta.validation.constraints.Past; // تاریخ تولد باید در گذشته باشد
import jakarta.validation.constraints.Pattern; // برای الگوهای خاص مثل کد ملی
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateUserProfileRequest {


    @Size(max = 100, message = "نام نمی‌تواند بیشتر از 100 کاراکتر باشد")
    private String firstName;

    @Size(max = 100, message = "نام خانوادگی نمی‌تواند بیشتر از 100 کاراکتر باشد")
    private String lastName;

    @Past(message = "تاریخ تولد باید در گذشته باشد")
    private LocalDate birthDate;

    @Pattern(regexp = "\\d{10}", message = "کد ملی باید 10 رقم باشد") // الگوی ۱۰ رقمی برای کد ملی
    private String nationalId;

    @Size(max = 100, message = "نام پدر نمی‌تواند بیشتر از 100 کاراکتر باشد")
    private String fatherName;

    // --- اطلاعات تماس ---
    @Pattern(regexp = "^09\\d{9}$", message = "فرمت شماره موبایل نامعتبر است (مثال: 09123456789)")
    private String mobilePhone;

    @Size(max = 15, message = "شماره تلفن ثابت طولانی است")
    private String landlinePhone;

    @Size(max = 100)
    private String province;
    @Size(max = 100)
    private String city;
    @Size(max = 255)
    private String street;
    @Size(max = 100)
    private String alley;
    @Size(max = 20)
    private String houseNumber;

    // --- اطلاعات فرهنگی ---
    @Size(max = 100)
    private String educationLevel;
    @Size(max = 100)
    private String fieldOfStudy;
    @Size(max = 100)
    private String occupation;

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


}