package com.example.project.receive;


import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class RecordReceiveRequest {

    @NotBlank(message = "شماره کارت مقصد الزامی است")
    @Pattern(regexp = "\\d{16}", message = "شماره کارت مقصد باید ۱۶ رقم باشد")
    private String destinationCardNumber;

    @NotNull(message = "مبلغ دریافتی الزامی است")
    @Positive(message = "مبلغ دریافتی باید مثبت باشد")
    private BigDecimal amount;

    @Size(max = 255, message = "توضیحات نمی‌تواند بیشتر از 255 کاراکتر باشد")
    private String description; // توضیحات (اختیاری)

    @PastOrPresent(message = "زمان دریافت نمی‌تواند در آینده باشد")
    private LocalDateTime receiveTime;

    public String getDestinationCardNumber() {
        return destinationCardNumber;
    }

    public void setDestinationCardNumber(String destinationCardNumber) {
        this.destinationCardNumber = destinationCardNumber;
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
}