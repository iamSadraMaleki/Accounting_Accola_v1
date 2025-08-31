package com.example.project.request;


import com.example.project.dashboard.OperationNotAllowedException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <<< برای محدود کردن به کاربر
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/requests")
public class UserRequestController {

    private static final Logger log = LoggerFactory.getLogger(UserRequestController.class);

    @Autowired
    private UserRequestService userRequestService;


    @PostMapping("/seller-upgrade")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> submitSellerUpgradeRequest(
            @Valid @RequestPart("requestData") SubmitSellerRequestDto requestDto,
            @RequestPart(value = "licenseFile", required = false) MultipartFile licenseFile) {
        log.info("Request received to submit seller upgrade request for user");
        try {
            userRequestService.submitSellerUpgradeRequest(requestDto, licenseFile);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "درخواست شما با موفقیت ثبت شد و در حال بررسی است."));
        } catch (OperationNotAllowedException e) {
            log.warn("Seller upgrade request failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", e.getMessage()));
        } catch (FileStorageException e) {
            log.error("Seller upgrade request failed due to file storage error", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطا در آپلود فایل: " + e.getMessage()));
        } catch (Exception e) {
            log.error("Error submitting seller upgrade request", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام ثبت درخواست."));
        }
    }

    @GetMapping("/my-history") // یک مسیر مشخص‌تر مثل /my-history یا فقط /
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN') or hasRole('BUSINESS') ")
    public ResponseEntity<List<SellerRequestDto>> getMyRequestHistory() {
        log.info("Request received to get request history for current user");
        try {
            List<SellerRequestDto> myRequests = userRequestService.getMyRequests();
            return ResponseEntity.ok(myRequests);
        } catch (Exception e) {
            log.error("Error fetching user request history", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList()); // لیست خالی در خطا
        }
    }
}