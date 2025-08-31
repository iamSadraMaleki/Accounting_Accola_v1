package com.example.project.transection;

import com.example.project.cardmanagment.BankCardService;
import com.example.project.cardmanagment.ResourceNotFoundException;
import com.example.project.payment.InsufficientBalanceException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);

    @Autowired
    private TransferService transferService;

    @Autowired
    private BankCardService bankCardService; // برای lookup


    @PostMapping
    public ResponseEntity<?> recordNewTransfer(@Valid @RequestBody CreateTransferRequest request) {
        log.info("Request received to record a new transfer from card ending {} to card ending {}",
                request.getSourceCardNumber().substring(request.getSourceCardNumber().length() - 4),
                request.getDestinationCardNumber().substring(request.getDestinationCardNumber().length() - 4));
        try {
            TransferDto savedTransfer = transferService.recordTransfer(request);
            // پاسخ 201 Created همراه با اطلاعات انتقال ثبت شده
            return ResponseEntity.status(HttpStatus.CREATED).body(savedTransfer);
        } catch (ResourceNotFoundException e) {
            log.warn("Transfer recording failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (InsufficientBalanceException e) {
            log.warn("Transfer recording failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Transfer recording failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error recording transfer", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام ثبت انتقال."));
        }
    }


    @GetMapping("/lookup/{cardNumber}")
    public ResponseEntity<?> lookupCardHolder(@PathVariable String cardNumber) {

        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            return ResponseEntity.badRequest().body(Map.of("error", "فرمت شماره کارت نامعتبر است."));
        }
        log.info("Request received for card lookup: ****{}", cardNumber.substring(cardNumber.length() - 4));
        try {
            CardLookupResponse response = bankCardService.lookupCardHolder(cardNumber);
            return ResponseEntity.ok(response);
        } catch (ResourceNotFoundException e) {
            log.info("Card lookup failed for ****{}: {}", cardNumber.substring(cardNumber.length() - 4), e.getMessage());

            return ResponseEntity.ok(new CardLookupResponse(null));
        } catch (Exception e) {
            log.error("Error during card lookup for ****{}", cardNumber.substring(cardNumber.length() - 4), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور."));
        }
    }

    @GetMapping
    public ResponseEntity<List<TransferDto>> getCurrentUserTransfers() {
        log.info("Request received to get transfer history for current user");
        try {
            List<TransferDto> transfers = transferService.getTransfersForCurrentUser();
            return ResponseEntity.ok(transfers);
        } catch (Exception e) {
            log.error("Error fetching transfer history", e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(List.of());
        }
    }

}
