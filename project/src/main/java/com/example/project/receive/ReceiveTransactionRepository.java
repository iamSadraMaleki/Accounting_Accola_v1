package com.example.project.receive;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReceiveTransactionRepository extends JpaRepository<ReceiveTransaction, Long> {


    List<ReceiveTransaction> findByUserIdOrderByReceiveTimeDesc(Long userId);


    List<ReceiveTransaction> findByDestinationBankCardIdOrderByReceiveTimeDesc(Long destinationCardId);


    List<ReceiveTransaction> findByUserIdAndReceiveTimeBetweenOrderByReceiveTimeDesc(Long userId, LocalDateTime startTime, LocalDateTime endTime);


}