package com.example.project.payment;

import com.example.project.cardmanagment.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat; // برای پارس کردن تاریخ از RequestParam
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // انوتیشن‌های RestController, RequestMapping, ...

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")

public class PaymentTransactionController {

    private static final Logger log = LoggerFactory.getLogger(PaymentTransactionController.class);

    @Autowired
    private PaymentTransactionService paymentTransactionService;


    @PostMapping
    public ResponseEntity<?> recordNewPayment(@Valid @RequestBody RecordPaymentRequest request) {
        log.info("Request received to record a new payment for shop: {}", request.getShopName());
        try {
            PaymentTransactionDto savedTransaction = paymentTransactionService.recordPayment(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransaction);
        } catch (ResourceNotFoundException e) {

            log.warn("Payment recording failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InsufficientBalanceException e) {

            log.warn("Payment recording failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {

            log.warn("Payment recording failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error recording payment", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام ثبت پرداخت."));
        }
    }


    @GetMapping
    public ResponseEntity<List<PaymentTransactionDto>> getAllCurrentUserPayments() {
        log.info("Request received to get all payments for current user");
        try {
            List<PaymentTransactionDto> transactions = paymentTransactionService.getTransactionsForCurrentUser();
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching user payments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/by-date")
    public ResponseEntity<List<PaymentTransactionDto>> getCurrentUserPaymentsByDate(
            @RequestParam("start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam("end") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        log.info("Request received to get payments for current user between {} and {}", startTime, endTime);
        try {
            List<PaymentTransactionDto> transactions = paymentTransactionService.getTransactionsForCurrentUserByDateRange(startTime, endTime);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("Error fetching user payments by date range", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
