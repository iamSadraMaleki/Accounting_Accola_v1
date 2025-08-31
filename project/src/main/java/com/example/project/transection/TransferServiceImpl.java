package com.example.project.transection; // یا پکیج سرویس شما


import com.example.project.cardmanagment.BankCard;
import com.example.project.cardmanagment.BankCardRepository;
import com.example.project.cardmanagment.ResourceNotFoundException;
import com.example.project.payment.InsufficientBalanceException;
import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import com.example.project.transection.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List; // برای متدهای آینده
import java.util.stream.Collectors; // برای متدهای آینده

@Service
public class TransferServiceImpl implements TransferService {

    private static final Logger log = LoggerFactory.getLogger(TransferServiceImpl.class);

    @Autowired private TransferTransactionRepository transferRepository;
    @Autowired private BankCardRepository bankCardRepository;
    @Autowired private UserRepository userRepository;

    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {

            log.warn("Attempted to mask invalid card number.");
            return "**** **** **** ****";
        }

        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }

    private TransferDto mapToDto(TransferTransaction tx) {
        if (tx == null) {
            return null;
        }
        TransferDto dto = new TransferDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setDescription(tx.getDescription());
        dto.setTransferTime(tx.getTransferTime());
        dto.setCreatedAt(tx.getCreatedAt());

        if (tx.getUser() != null) {
            dto.setUsername(tx.getUser().getUsername());
        } else {
            dto.setUsername("نامشخص");
        }


        if (tx.getSourceBankCard() != null) {
            dto.setSourceMaskedCardNumber(this.maskCardNumber(tx.getSourceBankCard().getCardNumber())); // << استفاده از متد محلی
            dto.setSourceBankName(tx.getSourceBankCard().getBankName());
        } else {
            dto.setSourceMaskedCardNumber("N/A");
            dto.setSourceBankName("N/A");
        }


        if (tx.getDestinationBankCard() != null) {
            dto.setDestinationMaskedCardNumber(this.maskCardNumber(tx.getDestinationBankCard().getCardNumber())); // << استفاده از متد محلی
            dto.setDestinationBankName(tx.getDestinationBankCard().getBankName());
        } else {

            log.warn("Destination bank card was null for transfer ID: {}", tx.getId());
            dto.setDestinationMaskedCardNumber("N/A");
            dto.setDestinationBankName("N/A");
        }

        return dto;
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر یافت نشد: " + username));
    }



    @Override
    @Transactional
    public TransferDto recordTransfer(CreateTransferRequest request) {
        User currentUser = getCurrentUser();
        log.info("Recording transfer for user: {}", currentUser.getUsername());

        BankCard sourceCard = bankCardRepository.findByCardNumberAndUser(request.getSourceCardNumber(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("کارت مبدأ یافت نشد یا متعلق به شما نیست."));
        log.debug("Found source card ID: {}", sourceCard.getId());


        BankCard destinationCard = bankCardRepository.findByCardNumber(request.getDestinationCardNumber())
                .orElseThrow(() -> new ResourceNotFoundException("کارت مقصد در سیستم یافت نشد."));

        log.debug("Found destination card ID: {}", destinationCard.getId());

        if (sourceCard.getId().equals(destinationCard.getId())) {
            throw new IllegalArgumentException("کارت مبدأ و مقصد نمی‌تواند یکسان باشد.");
        }

        if (sourceCard.getBalance().compareTo(request.getAmount()) < 0) {
            log.warn("Insufficient balance for source card ID {}", sourceCard.getId());
            throw new InsufficientBalanceException("موجودی کارت مبدأ کافی نیست.");
        }
        log.debug("Source balance sufficient for card ID: {}", sourceCard.getId());

        sourceCard.setBalance(sourceCard.getBalance().subtract(request.getAmount()));
        destinationCard.setBalance(destinationCard.getBalance().add(request.getAmount()));

        bankCardRepository.save(sourceCard);
        bankCardRepository.save(destinationCard);
        log.debug("Balances updated for source ID {} and destination ID {}", sourceCard.getId(), destinationCard.getId());

        TransferTransaction transaction = new TransferTransaction(
                request.getAmount(),
                request.getDescription(),
                LocalDateTime.now(),
                sourceCard,
                destinationCard,
                currentUser
        );
        TransferTransaction savedTransaction = transferRepository.save(transaction);
        log.info("Transfer transaction recorded with ID: {}", savedTransaction.getId());


        return mapToDto(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransferDto> getTransfersForCurrentUser() {
        User currentUser = getCurrentUser();
        log.info("Fetching ALL transfer history involving cards for user: {}", currentUser.getUsername());


        List<BankCard> userCards = bankCardRepository.findByUser(currentUser);


        if (userCards == null || userCards.isEmpty()) {
            log.info("User {} has no bank cards.", currentUser.getUsername());
            return Collections.emptyList();
        }


        List<Long> userCardIds = userCards.stream().map(BankCard::getId).collect(Collectors.toList());
        log.debug("User card IDs for transfer lookup: {}", userCardIds);


        List<TransferTransaction> transactions = transferRepository
                .findBySourceBankCard_IdInOrDestinationBankCard_IdInOrderByTransferTimeDesc(userCardIds, userCardIds);
        log.info("Found {} transfer transactions involving user {}", transactions.size(), currentUser.getUsername());


        return transactions.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }
}