package com.example.project.cardmanagment;

import com.example.project.transection.CardLookupResponse;

import java.util.List;

public interface BankCardService {


    BankCardDto addCard(CreateBankCardRequest request);


    List<BankCardDto> getCardsForCurrentUser();


    BankCardDto getCardByIdForCurrentUser(Long cardId);


    BankCardDto updateCard(Long cardId, UpdateBankCardRequest request);


    void deleteCard(Long cardId);


    List<BalanceStatusDto> getBalanceStatusForCurrentUser();

    CardLookupResponse lookupCardHolder(String cardNumber);

    String maskCardNumber(String cardNumber);

}
