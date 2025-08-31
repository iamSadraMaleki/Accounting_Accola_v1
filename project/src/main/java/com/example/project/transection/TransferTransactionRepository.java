package com.example.project.transection;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransferTransactionRepository extends JpaRepository<TransferTransaction, Long> {

    List<TransferTransaction> findBySourceBankCard_IdInOrDestinationBankCard_IdInOrderByTransferTimeDesc(List<Long> sourceCardIds, List<Long> destinationCardIds);
    List<TransferTransaction> findByUserIdOrderByTransferTimeDesc(Long userId);
    List<TransferTransaction> findBySourceBankCardIdOrderByTransferTimeDesc(Long sourceCardId);
    List<TransferTransaction> findByDestinationBankCardIdOrderByTransferTimeDesc(Long destinationCardId);

}
