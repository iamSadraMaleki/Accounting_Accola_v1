package com.example.project.cardmanagment;



import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UpdateBankCardRequest {



    @NotBlank(message = "نام بانک الزامی است")
    @Size(max = 100)
    private String bankName;

    @NotNull(message = "ماه انقضا الزامی است")
    @Min(1) @Max(12)
    private Integer expiryMonth;

    @NotNull(message = "سال انقضا الزامی است")
    @Min(1400) // یا سال میلادی
    private Integer expiryYear;

    @NotBlank(message = "نام صاحب کارت الزامی است")
    @Size(max = 150)
    private String cardHolderName;

     @NotNull(message = "موجودی کارت الزامی است")
     @DecimalMin(value = "0.0", inclusive = true)
     private BigDecimal balance;

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public Integer getExpiryMonth() {
        return expiryMonth;
    }

    public void setExpiryMonth(Integer expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public Integer getExpiryYear() {
        return expiryYear;
    }

    public void setExpiryYear(Integer expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
