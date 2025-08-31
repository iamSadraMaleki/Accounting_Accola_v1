package com.example.project.payment;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecordPaymentRequest {

    @NotBlank(message = "نام فروشگاه/گیرنده الزامی است")
    @Size(max = 200)
    private String shopName;


    @PastOrPresent(message = "زمان تراکنش نمی‌تواند در آینده باشد")
    private LocalDateTime transactionTime;

    @Size(max = 100)
    private String terminalInfo;

    @NotBlank(message = "شماره کارت پرداخت کننده الزامی است")
    @Pattern(regexp = "\\d{16}", message = "شماره کارت باید ۱۶ رقم باشد")
    private String cardNumber;

    @NotBlank(message = "شماره پیگیری/مرجع الزامی است")
    @Size(max = 50)
    private String trackingNumber;

    @NotNull(message = "مبلغ پرداخت الزامی است")
    @Positive(message = "مبلغ پرداخت باید مثبت باشد")
    private BigDecimal amount;

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

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
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
}
