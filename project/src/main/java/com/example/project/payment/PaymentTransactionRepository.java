package com.example.project.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentTransactionRepository extends JpaRepository<PaymentTransaction, Long> {


    List<PaymentTransaction> findByUserIdOrderByTransactionTimeDesc(Long userId);


    List<PaymentTransaction> findByBankCardIdOrderByTransactionTimeDesc(Long bankCardId);


    List<PaymentTransaction> findByUserIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(Long userId, LocalDateTime startTime, LocalDateTime endTime);


    List<PaymentTransaction> findByBankCardIdAndTransactionTimeBetweenOrderByTransactionTimeDesc(Long bankCardId, LocalDateTime startTime, LocalDateTime endTime);

}