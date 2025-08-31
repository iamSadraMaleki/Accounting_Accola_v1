package com.example.project.request;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SubmitSellerRequestDto {



    @NotBlank(message = "نام کسب و کار الزامی است")
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

    @NotBlank(message = "آدرس محل کار الزامی است")
    @Size(max = 500)
    private String workAddress;

    @NotBlank(message = "تلفن محل کار الزامی است")
    @Size(max = 20)
    private String workPhone;



    @NotBlank(message = "متن درخواست الزامی است")
    @Size(max = 2000)
    private String requestDetails;

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

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }
}
