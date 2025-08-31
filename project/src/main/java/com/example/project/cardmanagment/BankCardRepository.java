package com.example.project.cardmanagment;


import com.example.project.signup.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BankCardRepository extends JpaRepository<BankCard, Long> { // BankCard و نوع ID آن (Long)


    List<BankCard> findByUser(User user);


    List<BankCard> findByUserId(Long userId);


    Optional<BankCard> findByCardNumberAndUser(String cardNumber, User user);


    Optional<BankCard> findByIdAndUser(Long id, User user);

    Optional<BankCard> findByCardNumber(String cardNumber);

}
