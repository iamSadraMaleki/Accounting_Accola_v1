package com.example.project.transection;


import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TransferDto {
    private Long id;
    private BigDecimal amount;
    private String description;
    private LocalDateTime transferTime;
    private String sourceMaskedCardNumber;
    private String sourceBankName;
    private String destinationMaskedCardNumber;
    private String destinationBankName;
    private String username;
    private LocalDateTime createdAt;



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

    public String getSourceMaskedCardNumber() {
        return sourceMaskedCardNumber;
    }

    public void setSourceMaskedCardNumber(String sourceMaskedCardNumber) {
        this.sourceMaskedCardNumber = sourceMaskedCardNumber;
    }

    public String getSourceBankName() {
        return sourceBankName;
    }

    public void setSourceBankName(String sourceBankName) {
        this.sourceBankName = sourceBankName;
    }

    public String getDestinationMaskedCardNumber() {
        return destinationMaskedCardNumber;
    }

    public void setDestinationMaskedCardNumber(String destinationMaskedCardNumber) {
        this.destinationMaskedCardNumber = destinationMaskedCardNumber;
    }

    public String getDestinationBankName() {
        return destinationBankName;
    }

    public void setDestinationBankName(String destinationBankName) {
        this.destinationBankName = destinationBankName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}