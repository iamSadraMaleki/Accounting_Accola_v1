package com.example.project.request;


import com.example.project.cardmanagment.ResourceNotFoundException;
import com.example.project.dashboard.OperationNotAllowedException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // <<< برای امنیت ادمین
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/requests")

@PreAuthorize("hasRole('ADMIN')")
public class AdminRequestController {

    private static final Logger log = LoggerFactory.getLogger(AdminRequestController.class);

    @Autowired
    private AdminRequestService adminRequestService;


    @GetMapping("/all")
    public ResponseEntity<List<SellerRequestDto>> getAllSellerRequests() {

        log.info("Admin request received to get all seller requests");
        List<SellerRequestDto> requests = adminRequestService.getAllRequests();
        return ResponseEntity.ok(requests);

    }

    @GetMapping("/pending")
    public ResponseEntity<List<SellerRequestDto>> getPendingSellerRequests() {
        log.info("Admin request received to get pending seller requests");
        List<SellerRequestDto> requests = adminRequestService.getPendingRequests();
        return ResponseEntity.ok(requests);
    }


    @GetMapping("/{requestId}")
    public ResponseEntity<?> getSellerRequestById(@PathVariable Long requestId) {
        log.info("Admin request received to get seller request with ID: {}", requestId);

        try {
            SellerRequestDto requestDto = adminRequestService.getRequestById(requestId);
            return ResponseEntity.ok(requestDto);
        } catch (ResourceNotFoundException e) {
            log.warn("Admin request failed for ID {}: {}", requestId, e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error fetching request by ID: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور"));
        }
    }


    @PutMapping("/{requestId}/approve")
    public ResponseEntity<?> approveSellerRequest(@PathVariable Long requestId, @Valid @RequestBody UpdateRequestStatusRequest requestDto) {
        log.info("Admin request received to APPROVE seller request ID: {}", requestId);

        try {
            SellerRequestDto approvedRequest = adminRequestService.approveRequest(requestId, requestDto);
            return ResponseEntity.ok(approvedRequest);
        } catch (ResourceNotFoundException e) {
            log.warn("Approve failed for ID {}: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (OperationNotAllowedException e) {
            log.warn("Approve failed for ID {}: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error approving request ID: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی هنگام تایید درخواست"));
        }
    }


    @PutMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectSellerRequest(@PathVariable Long requestId, @Valid @RequestBody UpdateRequestStatusRequest requestDto) {
        log.info("Admin request received to REJECT seller request ID: {}", requestId);
        try {
            SellerRequestDto rejectedRequest = adminRequestService.rejectRequest(requestId, requestDto);
            return ResponseEntity.ok(rejectedRequest);
        } catch (ResourceNotFoundException e) {
            log.warn("Reject failed for ID {}: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (OperationNotAllowedException e) {
            log.warn("Reject failed for ID {}: {}", requestId, e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error rejecting request ID: {}", requestId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی هنگام رد درخواست"));
        }
    }

}