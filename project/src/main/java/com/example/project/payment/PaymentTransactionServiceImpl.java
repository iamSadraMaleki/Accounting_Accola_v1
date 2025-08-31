package com.example.project.payment;


import com.example.project.cardmanagment.BankCard;
import com.example.project.cardmanagment.BankCardRepository;
import com.example.project.cardmanagment.ResourceNotFoundException;
import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <<< مهم برای عملیات ترکیبی

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentTransactionServiceImpl implements PaymentTransactionService {

    private static final Logger log = LoggerFactory.getLogger(PaymentTransactionServiceImpl.class);

    @Autowired
    private PaymentTransactionRepository transactionRepository;

    @Autowired
    private BankCardRepository bankCardRepository;

    @Autowired
    private UserRepository userRepository;


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("User not found in getCurrentUser for username: {}", username);
                    return new UsernameNotFoundException("کاربر لاگین کرده یافت نشد: " + username);
                });
    }


    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) return "****";
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }


    private PaymentTransactionDto mapToDto(PaymentTransaction tx) {
        if (tx == null) return null;
        PaymentTransactionDto dto = new PaymentTransactionDto();
        dto.setId(tx.getId());
        dto.setShopName(tx.getShopName());
        dto.setTransactionTime(tx.getTransactionTime());
        dto.setTerminalInfo(tx.getTerminalInfo());
        dto.setTrackingNumber(tx.getTrackingNumber());
        dto.setAmount(tx.getAmount());
        dto.setCreatedAt(tx.getCreatedAt());
        if (tx.getBankCard() != null) { // اضافه کردن اطلاعات کارت
            dto.setMaskedCardNumber(maskCardNumber(tx.getBankCard().getCardNumber()));
            dto.setBankName(tx.getBankCard().getBankName());
        }
        // if (tx.getUser() != null) { dto.setUsername(tx.getUser().getUsername()); } // اگر لازم بود
        return dto;
    }


    @Override
    @Transactional
    public PaymentTransactionDto recordPayment(RecordPaymentRequest request) {
        User currentUser = getCurrentUser();
        log.info("Recording payment for user: {}", currentUser.getUsername());


        BankCard card = bankCardRepository.findByCardNumberAndUser(request.getCardNumber(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "کارت بانکی با شماره " + maskCardNumber(request.getCardNumber()) + " برای شما یافت نشد."
                ));
        log.debug("Found bank card with ID: {}", card.getId());


        if (card.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Insufficient balance for card ID {} (Balance: {}, Requested: {})",
                    card.getId(), card.getBalance(), request.getAmount());
            throw new InsufficientBalanceException(
                    "موجودی کارت " + maskCardNumber(card.getCardNumber()) + " کافی نیست."
            );
        }
        log.debug("Balance sufficient for card ID: {}", card.getId());


        BigDecimal newBalance = card.getBalance().subtract(request.getAmount());
        card.setBalance(newBalance);

        bankCardRepository.save(card);
        log.debug("Updated balance for card ID {}: {}", card.getId(), newBalance);


        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setShopName(request.getShopName());

        transaction.setTransactionTime(request.getTransactionTime() != null ? request.getTransactionTime() : LocalDateTime.now());
        transaction.setTerminalInfo(request.getTerminalInfo());
        transaction.setTrackingNumber(request.getTrackingNumber());
        transaction.setAmount(request.getAmount());
        transaction.setBankCard(card);
        transaction.setUser(currentUser);

        PaymentTransaction savedTransaction = transactionRepository.save(transaction);
        log.info("Payment transaction recorded with ID: {}", savedTransaction.getId());

        return mapToDto(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionDto> getTransactionsForCurrentUser() {
        User currentUser = getCurrentUser();
        List<PaymentTransaction> transactions = transactionRepository.findByUserIdOrderByTransactionTimeDesc(currentUser.getId());
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PaymentTransactionDto> getTransactionsForCurrentUserByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        User currentUser = getCurrentUser();
        List<PaymentTransaction> transactions = transactionRepository.findByUserIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(currentUser.getId(), startTime, endTime);
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
