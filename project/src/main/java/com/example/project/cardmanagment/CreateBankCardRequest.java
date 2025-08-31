package com.example.project.cardmanagment;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateBankCardRequest {

    @NotBlank(message = "شماره کارت الزامی است")
    @Pattern(regexp = "\\d{16}", message = "شماره کارت باید 16 رقم باشد")
    private String cardNumber;

    @NotBlank(message = "نام بانک الزامی است")
    @Size(max = 100)
    private String bankName;

    @NotNull(message = "ماه انقضا الزامی است")
    @Min(value = 1, message = "ماه انقضا باید بین 1 تا 12 باشد")
    @Max(value = 12, message = "ماه انقضا باید بین 1 تا 12 باشد")
    private Integer expiryMonth;

    @NotNull(message = "سال انقضا الزامی است")
    @Min(value = 1400, message = "سال انقضا نامعتبر است") // یا سال میلادی متناسب
    private Integer expiryYear; // اعتبار سنجی تاریخ انقضا (آیا گذشته یا نه) در سرویس انجام شود

    @NotBlank(message = "نام صاحب کارت الزامی است")
    @Size(max = 150)
    private String cardHolderName;

    @NotNull(message = "موجودی اولیه کارت الزامی است")
    @DecimalMin(value = "0.0", inclusive = true, message = "موجودی نمی‌تواند منفی باشد")
    private BigDecimal initialBalance = BigDecimal.ZERO;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

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

    public BigDecimal getInitialBalance() {
        return initialBalance;
    }

    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
}