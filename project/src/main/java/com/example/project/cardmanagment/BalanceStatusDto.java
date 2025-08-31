package com.example.project.cardmanagment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
public class BalanceStatusDto {
    private String bankName;
    private String maskedCardNumber;
    private BigDecimal balance;

    public BalanceStatusDto() {
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public BalanceStatusDto(String bankName, String maskedCardNumber, BigDecimal balance) {
        this.bankName = bankName;
        this.maskedCardNumber = maskedCardNumber;
        this.balance = balance;
    }
}
