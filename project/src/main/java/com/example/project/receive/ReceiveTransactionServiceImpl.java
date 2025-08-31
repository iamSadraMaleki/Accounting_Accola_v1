package com.example.project.receive;

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
import org.springframework.transaction.annotation.Transactional; // << مهم
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReceiveTransactionServiceImpl implements ReceiveTransactionService {

    private static final Logger log = LoggerFactory.getLogger(ReceiveTransactionServiceImpl.class);

    @Autowired private ReceiveTransactionRepository receiveRepository; // << ریپازیتوری جدید
    @Autowired private BankCardRepository bankCardRepository;
    @Autowired private UserRepository userRepository;


    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر یافت نشد: " + username));
    }


    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) return "****";
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }


    private ReceiveTransactionDto mapToDto(ReceiveTransaction tx) {
        if (tx == null) return null;
        ReceiveTransactionDto dto = new ReceiveTransactionDto();
        dto.setId(tx.getId());
        dto.setAmount(tx.getAmount());
        dto.setDescription(tx.getDescription());
        dto.setReceiveTime(tx.getReceiveTime());
        dto.setCreatedAt(tx.getCreatedAt());
        if (tx.getUser() != null) {
            dto.setUsername(tx.getUser().getUsername());
        }
        if (tx.getDestinationBankCard() != null) {
            dto.setDestinationMaskedCardNumber(maskCardNumber(tx.getDestinationBankCard().getCardNumber()));
            dto.setDestinationBankName(tx.getDestinationBankCard().getBankName());
        } else {
            dto.setDestinationMaskedCardNumber("N/A");
            dto.setDestinationBankName("N/A");
        }
        return dto;
    }

    @Override
    @Transactional
    public ReceiveTransactionDto recordReceive(RecordReceiveRequest request) {
        User currentUser = getCurrentUser();
        log.info("Recording receive transaction for user: {}", currentUser.getUsername());


        BankCard destinationCard = bankCardRepository.findByCardNumberAndUser(request.getDestinationCardNumber(), currentUser)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "کارت مقصد " + maskCardNumber(request.getDestinationCardNumber()) + " متعلق به شما نیست یا یافت نشد."
                ));
        log.debug("Found destination card ID: {}", destinationCard.getId());


        BigDecimal newBalance = destinationCard.getBalance().add(request.getAmount());
        destinationCard.setBalance(newBalance);


        bankCardRepository.save(destinationCard);
        log.debug("Updated balance for destination card ID {}: {}", destinationCard.getId(), newBalance);


        ReceiveTransaction transaction = new ReceiveTransaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());

        transaction.setReceiveTime(request.getReceiveTime() != null ? request.getReceiveTime() : LocalDateTime.now());
        transaction.setDestinationBankCard(destinationCard);
        transaction.setUser(currentUser);

        ReceiveTransaction savedTransaction = receiveRepository.save(transaction);
        log.info("Receive transaction recorded with ID: {}", savedTransaction.getId());


        return mapToDto(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceiveTransactionDto> getReceivesForCurrentUser() {
        User currentUser = getCurrentUser();
        log.info("Fetching receive history for user: {}", currentUser.getUsername());
        List<ReceiveTransaction> transactions = receiveRepository.findByUserIdOrderByReceiveTimeDesc(currentUser.getId());
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReceiveTransactionDto> getReceivesForCurrentUserByDateRange(LocalDateTime startTime, LocalDateTime endTime) {
        User currentUser = getCurrentUser();
        log.info("Fetching receive history for user {} between {} and {}", currentUser.getUsername(), startTime, endTime);
        List<ReceiveTransaction> transactions = receiveRepository.findByUserIdAndReceiveTimeBetweenOrderByReceiveTimeDesc(currentUser.getId(), startTime, endTime);
        return transactions.stream().map(this::mapToDto).collect(Collectors.toList());
    }
}
