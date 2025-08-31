package com.example.project.request;


import com.example.project.signup.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp; // برای زمان پاسخ‌دهی

import java.time.LocalDateTime;

@Entity
@Table(name = "seller_requests")
@Data
public class SellerRequest {

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "کاربر درخواست دهنده الزامی است")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, updatable = false)
    private User user;

    @NotBlank(message = "جزئیات درخواست الزامی است")
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String requestDetails;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RequestStatus status = RequestStatus.PENDING;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String adminNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "resolved_by_user_id", nullable = true)
    private User resolvedBy;

    @Column(nullable = true)
    private LocalDateTime resolutionTime;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime requestTime;



     public SellerRequest() {}

    public SellerRequest(User user, String requestDetails) {
        this.user = user;
        this.requestDetails = requestDetails;
        this.status = RequestStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getRequestDetails() {
        return requestDetails;
    }

    public void setRequestDetails(String requestDetails) {
        this.requestDetails = requestDetails;
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        this.status = status;
    }

    public String getAdminNotes() {
        return adminNotes;
    }

    public void setAdminNotes(String adminNotes) {
        this.adminNotes = adminNotes;
    }

    public User getResolvedBy() {
        return resolvedBy;
    }

    public void setResolvedBy(User resolvedBy) {
        this.resolvedBy = resolvedBy;
    }

    public LocalDateTime getResolutionTime() {
        return resolutionTime;
    }

    public void setResolutionTime(LocalDateTime resolutionTime) {
        this.resolutionTime = resolutionTime;
    }

    public LocalDateTime getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(LocalDateTime requestTime) {
        this.requestTime = requestTime;
    }
}
