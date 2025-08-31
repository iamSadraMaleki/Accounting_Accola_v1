package com.example.project.request;


import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateRequestStatusRequest {
    @Size(max = 1000, message = "یادداشت ادمین نمی‌تواند بیشتر از 1000 کاراکتر باشد")
    private String adminNotes;

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }
}