package com.example.project.cardmanagment;


import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // انوتیشن‌های RestController, RequestMapping, ...

import java.util.List;
import java.util.Map; // برای برگرداندن پیام خطا به صورت JSON

@RestController
@RequestMapping("/api/cards")
public class BankCardController {

    private static final Logger log = LoggerFactory.getLogger(BankCardController.class);

    @Autowired
    private BankCardService bankCardService;


    @PostMapping
    public ResponseEntity<?> addBankCard(@Valid @RequestBody CreateBankCardRequest request) {
        log.info("Request received to add a new bank card");
        try {
            BankCardDto newCard = bankCardService.addCard(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(newCard);
        } catch (IllegalArgumentException e) {

            log.warn("Failed to add bank card: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error adding bank card", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام افزودن کارت."));
        }
    }


    @GetMapping
    public ResponseEntity<List<BankCardDto>> getCurrentUserCards() {
        log.info("Request received to get all cards for current user");
        List<BankCardDto> cards = bankCardService.getCardsForCurrentUser();
        return ResponseEntity.ok(cards);
    }


    @GetMapping("/{cardId}")
    public ResponseEntity<?> getCardById(@PathVariable Long cardId) {
        log.info("Request received to get card with ID: {}", cardId);
        try {
            BankCardDto card = bankCardService.getCardByIdForCurrentUser(cardId);
            return ResponseEntity.ok(card);
        } catch (ResourceNotFoundException e) {
            log.warn("Card not found or access denied for ID {}: {}", cardId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error retrieving card with ID: {}", cardId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور."));
        }
    }


    @PutMapping("/{cardId}")
    public ResponseEntity<?> updateBankCard(@PathVariable Long cardId, @Valid @RequestBody UpdateBankCardRequest request) {
        log.info("Request received to update card with ID: {}", cardId);
        try {
            BankCardDto updatedCard = bankCardService.updateCard(cardId, request);
            return ResponseEntity.ok(updatedCard);
        } catch (ResourceNotFoundException e) {
            log.warn("Update failed - Card not found or access denied for ID {}: {}", cardId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (IllegalArgumentException e) {
            log.warn("Update failed for card ID {}: {}", cardId, e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating card with ID: {}", cardId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام آپدیت کارت."));
        }
    }


    @DeleteMapping("/{cardId}")
    public ResponseEntity<?> deleteBankCard(@PathVariable Long cardId) {
        log.info("Request received to delete card with ID: {}", cardId);
        try {
            bankCardService.deleteCard(cardId);
            // پاسخ 204 No Content معمولا برای حذف موفق استفاده میشه
            // یا میتونید 200 OK با یک پیام برگردونید
            // return ResponseEntity.noContent().build();
            return ResponseEntity.ok(Map.of("message", "کارت با موفقیت حذف شد."));
        } catch (ResourceNotFoundException e) {
            log.warn("Delete failed - Card not found or access denied for ID {}: {}", cardId, e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error deleting card with ID: {}", cardId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام حذف کارت."));
        }
    }


    @GetMapping("/balance-status")
    public ResponseEntity<List<BalanceStatusDto>> getBalanceStatus() {
        log.info("Request received for balance status");
        List<BalanceStatusDto> balanceStatusList = bankCardService.getBalanceStatusForCurrentUser();
        return ResponseEntity.ok(balanceStatusList);
    }


     @ExceptionHandler(ResourceNotFoundException.class)
     public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex) {
         log.warn("Resource not found: {}", ex.getMessage());
         return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", ex.getMessage()));
     }

     @ExceptionHandler(IllegalArgumentException.class)
     public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException ex) {
          log.warn("Bad Request: {}", ex.getMessage());
         return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", ex.getMessage()));
     }

}
