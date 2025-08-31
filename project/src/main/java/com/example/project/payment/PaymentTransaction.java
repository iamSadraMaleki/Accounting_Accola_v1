package com.example.project.payment;


import com.example.project.cardmanagment.BankCard;
import com.example.project.signup.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent; // زمان تراکنش نباید در آینده باشد
import jakarta.validation.constraints.Positive; // مبلغ باید مثبت باشد
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_transactions")
@Data
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "نام فروشگاه/گیرنده الزامی است")
    @Size(max = 200)
    @Column(nullable = false)
    private String shopName;



    @Column(nullable = false)
    private LocalDateTime transactionTime;

    @Size(max = 100)
    private String terminalInfo;

    @NotBlank(message = "شماره پیگیری/مرجع الزامی است")
    @Size(max = 50)
    @Column(nullable = false)
    private String trackingNumber;

    @NotNull(message = "مبلغ پرداخت الزامی است")
    @Positive(message = "مبلغ پرداخت باید مثبت باشد")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_card_id", nullable = false)
    private BankCard bankCard;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public PaymentTransaction(String shopName, LocalDateTime transactionTime, String terminalInfo, String trackingNumber, BigDecimal amount, BankCard bankCard, User user) {
        this.shopName = shopName;
        this.transactionTime = transactionTime;
        this.terminalInfo = terminalInfo;
        this.trackingNumber = trackingNumber;
        this.amount = amount;
        this.bankCard = bankCard;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getTerminalInfo() {
        return terminalInfo;
    }

    public void setTerminalInfo(String terminalInfo) {
        this.terminalInfo = terminalInfo;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public BankCard getBankCard() {
        return bankCard;
    }

    public void setBankCard(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public PaymentTransaction() {
    }
}
