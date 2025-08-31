package com.example.project.receive;


import java.time.LocalDateTime;
import java.util.List;

public interface ReceiveTransactionService {


    ReceiveTransactionDto recordReceive(RecordReceiveRequest request);

    List<ReceiveTransactionDto> getReceivesForCurrentUser();

    List<ReceiveTransactionDto> getReceivesForCurrentUserByDateRange(LocalDateTime startTime, LocalDateTime endTime);
}
