package com.example.project.request;


import lombok.Data;
//import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
public class SellerRequestDto {
    private Long id;
    private LocalDateTime requestTime;
    private SellerRequest.RequestStatus status;
    private String requestDetails;
    private String adminNotes;
    private LocalDateTime resolutionTime;
    private Long requestingUserId;
    private String requestingUsername;
    private String resolverUsername;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }

    public SellerRequest.RequestStatus getStatus() {
        return status;
    }

    public void setStatus(SellerRequest.RequestStatus status) {
        this.status = status;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public LocalDateTime getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(LocalDateTime resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    public Long getRequestingUserId() {
        return requestingUserId;
    }

    public void setRequestingUserId(Long requestingUserId) {
        this.requestingUserId = requestingUserId;
    }

    public String getRequestingUsername() {
        return requestingUsername;
    }

    public void setRequestingUsername(String requestingUsername) {
        this.requestingUsername = requestingUsername;
    }

    public String getResolverUsername() {
        return resolverUsername;
    }

    public void setResolverUsername(String resolverUsername) {
        this.resolverUsername = resolverUsername;
    }

    public SellerRequestDto() {
    }

}