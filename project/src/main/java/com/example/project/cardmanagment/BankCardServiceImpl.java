
package com.example.project.cardmanagment;


import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import com.example.project.transection.CardLookupResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BankCardServiceImpl implements BankCardService {

    private static final Logger log = LoggerFactory.getLogger(BankCardServiceImpl.class);

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


    @Override // <<< اضافه شد (چون در اینترفیس هست)
    public String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() != 16) {
            log.warn("Attempted to mask invalid card number.");
            return "**** **** **** ****";
        }
        return cardNumber.substring(0, 6) + "******" + cardNumber.substring(12);
    }

    private BankCardDto mapToDto(BankCard card) {
        if (card == null) return null;
        BankCardDto dto = new BankCardDto();
        dto.setId(card.getId());

        dto.setMaskedCardNumber(this.maskCardNumber(card.getCardNumber()));

        dto.setBankName(card.getBankName());
        dto.setExpiryMonth(card.getExpiryMonth());
        dto.setExpiryYear(card.getExpiryYear());
        dto.setCardHolderName(card.getCardHolderName());
        dto.setBalance(card.getBalance());
        dto.setCreatedAt(card.getCreatedAt());
        dto.setUpdatedAt(card.getUpdatedAt());

        return dto;
    }


    private BalanceStatusDto mapToBalanceDto(BankCard card) {
        if (card == null) return null;
        return new BalanceStatusDto(
                card.getBankName(),

                this.maskCardNumber(card.getCardNumber()),

                card.getBalance()
        );
    }


    private boolean isExpiryDateValid(Integer shamsiMonth, Integer shamsiYear) {

        if (shamsiMonth == null || shamsiYear == null || shamsiMonth < 1 || shamsiMonth > 12) return false;
        try {
            int currentGregorianYear = YearMonth.now().getYear();
            int currentShamsiYearApprox = currentGregorianYear - 621;
            if (shamsiYear < currentShamsiYearApprox) return false;
            if (shamsiYear == currentShamsiYearApprox) {
                int currentGregorianMonth = YearMonth.now().getMonthValue();
                if (shamsiMonth < currentGregorianMonth) return false;
            }
            log.warn("Expiry date validation is basic...");
            return true;
        } catch (Exception e) { return false; }
    }



    @Override
    @Transactional
    public BankCardDto addCard(CreateBankCardRequest request) {
        User currentUser = getCurrentUser();
        bankCardRepository.findByCardNumberAndUser(request.getCardNumber(), currentUser).ifPresent(c -> {
            throw new IllegalArgumentException("این شماره کارت قبلاً برای شما ثبت شده است.");
        });
        if (!isExpiryDateValid(request.getExpiryMonth(), request.getExpiryYear())) {
            throw new IllegalArgumentException("تاریخ انقضای کارت نامعتبر یا گذشته است.");
        }
        BankCard newCard = new BankCard();
        newCard.setCardNumber(request.getCardNumber());
        newCard.setBankName(request.getBankName());
        newCard.setExpiryMonth(request.getExpiryMonth());
        newCard.setExpiryYear(request.getExpiryYear());
        newCard.setCardHolderName(request.getCardHolderName());
        newCard.setBalance(request.getInitialBalance());
        newCard.setUser(currentUser);
        BankCard savedCard = bankCardRepository.save(newCard);
        log.info("New bank card added with ID {} for user {}", savedCard.getId(), currentUser.getUsername());
        return mapToDto(savedCard);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BankCardDto> getCardsForCurrentUser() {
        User currentUser = getCurrentUser();
        List<BankCard> cards = bankCardRepository.findByUser(currentUser);
        return cards.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public BankCardDto getCardByIdForCurrentUser(Long cardId) {
        User currentUser = getCurrentUser();
        BankCard card = bankCardRepository.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("کارت بانکی با شناسه " + cardId + " برای این کاربر یافت نشد."));
        return mapToDto(card);
    }

    @Override
    @Transactional
    public BankCardDto updateCard(Long cardId, UpdateBankCardRequest request) {
        User currentUser = getCurrentUser();
        BankCard card = bankCardRepository.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("کارت بانکی با شناسه " + cardId + " برای این کاربر یافت نشد یا دسترسی ندارید."));
        if (!isExpiryDateValid(request.getExpiryMonth(), request.getExpiryYear())) {
            throw new IllegalArgumentException("تاریخ انقضای جدید کارت نامعتبر یا گذشته است.");
        }
        card.setBankName(request.getBankName());
        card.setExpiryMonth(request.getExpiryMonth());
        card.setExpiryYear(request.getExpiryYear());
        card.setCardHolderName(request.getCardHolderName());
        BankCard updatedCard = bankCardRepository.save(card);
        log.info("Bank card with ID {} updated for user {}", updatedCard.getId(), currentUser.getUsername());
        return mapToDto(updatedCard);
    }

    @Override
    @Transactional
    public void deleteCard(Long cardId) {
        User currentUser = getCurrentUser();
        BankCard card = bankCardRepository.findByIdAndUser(cardId, currentUser)
                .orElseThrow(() -> new ResourceNotFoundException("کارت بانکی با شناسه " + cardId + " برای این کاربر یافت نشد یا دسترسی ندارید."));
        bankCardRepository.delete(card);
        log.info("Bank card with ID {} deleted for user {}", cardId, currentUser.getUsername());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BalanceStatusDto> getBalanceStatusForCurrentUser() {
        User currentUser = getCurrentUser();
        List<BankCard> cards = bankCardRepository.findByUser(currentUser);

        return cards.stream().map(this::mapToBalanceDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CardLookupResponse lookupCardHolder(String cardNumber) {
        log.warn("Executing potentially insecure card lookup for number ending in: {}",
                (cardNumber != null && cardNumber.length() > 4) ? cardNumber.substring(cardNumber.length() - 4) : "****");
        BankCard card = bankCardRepository.findByCardNumber(cardNumber)
                .orElseThrow(() -> new ResourceNotFoundException("کارتی با این شماره در سیستم یافت نشد."));
        return new CardLookupResponse(card.getCardHolderName());
    }


}