package com.example.project.transection;



import com.example.project.cardmanagment.BankCard;
import com.example.project.signup.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transfer_transactions")
@Data

public class TransferTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "مبلغ انتقال الزامی است")
    @Positive(message = "مبلغ انتقال باید مثبت باشد")
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Size(max = 255)
    private String description;

    @NotNull(message = "زمان انتقال الزامی است")
    @Column(nullable = false)
    private LocalDateTime transferTime;


    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_card_id", nullable = false)
    private BankCard sourceBankCard;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_card_id", nullable = false)
    private BankCard destinationBankCard;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public TransferTransaction(BigDecimal amount, String description, LocalDateTime transferTime, BankCard sourceBankCard, BankCard destinationBankCard, User user) {
        this.amount = amount;
        this.description = description;
        this.transferTime = transferTime;
        this.sourceBankCard = sourceBankCard;
        this.destinationBankCard = destinationBankCard;
        this.user = user;
    }

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

    public LocalDateTime getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(LocalDateTime transferTime) {
        this.transferTime = transferTime;
    }

    public BankCard getSourceBankCard() {
        return sourceBankCard;
    }

    public void setSourceBankCard(BankCard sourceBankCard) {
        this.sourceBankCard = sourceBankCard;
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
    public TransferTransaction(){

    }
}
