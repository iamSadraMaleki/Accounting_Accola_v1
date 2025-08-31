package com.example.project.receive;


import com.example.project.cardmanagment.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/receives")

public class ReceiveTransactionController {

    private static final Logger log = LoggerFactory.getLogger(ReceiveTransactionController.class);

    @Autowired
    private ReceiveTransactionService receiveTransactionService;


    @PostMapping
    public ResponseEntity<?> recordNewReceive(@Valid @RequestBody RecordReceiveRequest request) {
        log.info("Request received to record a new receive transaction");
        try {
            ReceiveTransactionDto savedReceive = receiveTransactionService.recordReceive(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedReceive);
        } catch (ResourceNotFoundException e) {

            log.warn("Receive recording failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {

            log.warn("Receive recording failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error recording receive transaction", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام ثبت دریافت."));
        }
    }


    @GetMapping
    public ResponseEntity<List<ReceiveTransactionDto>> getAllCurrentUserReceives() {
        log.info("Request received to get all receive transactions for current user");
        try {
            List<ReceiveTransactionDto> transactions = receiveTransactionService.getReceivesForCurrentUser();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching user receive transactions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of()); // برگرداندن لیست خالی در خطا
        }
    }


    @GetMapping("/by-date")
    public ResponseEntity<List<ReceiveTransactionDto>> getCurrentUserReceivesByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Request received to get receive transactions for current user between {} and {}", startTime, endTime);
        try {
            List<ReceiveTransactionDto> transactions = receiveTransactionService.getReceivesForCurrentUserByDateRange(startTime, endTime);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching user receive transactions by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(List.of());
        }
    }


}
