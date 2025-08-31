package com.example.project.payment;


import java.time.LocalDateTime;
import java.util.List;

public interface PaymentTransactionService {


    PaymentTransactionDto recordPayment(RecordPaymentRequest request);

    List<PaymentTransactionDto> getTransactionsForCurrentUser();

    List<PaymentTransactionDto> getTransactionsForCurrentUserByDateRange(LocalDateTime startTime, LocalDateTime endTime);

}
