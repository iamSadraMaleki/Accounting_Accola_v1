package com.example.project.receive;


import com.example.project.cardmanagment.BankCard;
import com.example.project.signup.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank; // شاید برای description لازم نباشه
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "receive_transactions")
@Data
public class ReceiveTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "مبلغ دریافتی الزامی است")
    @Positive(message = "مبلغ دریافتی باید مثبت باشد")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount; // مبلغ دریافت شده

    @Size(max = 255)
    private String description;

    @NotNull(message = "زمان دریافت الزامی است")
    @PastOrPresent(message = "زمان دریافت نمی‌تواند در آینده باشد")
    @Column(nullable = false)
    private LocalDateTime receiveTime;


    @NotNull(message = "کارت دریافت کننده وجه الزامی است")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_card_id", nullable = false)
    private BankCard destinationBankCard;

    @NotNull(message = "کاربر دریافت کننده الزامی است")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;


    public ReceiveTransaction(BigDecimal amount, String description, LocalDateTime receiveTime, BankCard destinationBankCard, User user) {
        this.amount = amount;
        this.description = description;
        this.receiveTime = receiveTime;
        this.destinationBankCard = destinationBankCard;
        this.user = user;
    }
     public ReceiveTransaction() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getReceiveTime() {
        return receiveTime;
    }

    public void setReceiveTime(LocalDateTime receiveTime) {
        this.receiveTime = receiveTime;
    }

    public BankCard getDestinationBankCard() {
        return destinationBankCard;
    }

    public void setDestinationBankCard(BankCard destinationBankCard) {
        this.destinationBankCard = destinationBankCard;
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
}