package com.example.project.transection;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateTransferRequest {

    @NotBlank(message = "شماره کارت مبدأ الزامی است")
    @Pattern(regexp = "\\d{16}", message = "شماره کارت مبدأ باید ۱۶ رقم باشد")
    private String sourceCardNumber;

    @NotBlank(message = "شماره کارت مقصد الزامی است")
    @Pattern(regexp = "\\d{16}", message = "شماره کارت مقصد باید ۱۶ رقم باشد")
    private String destinationCardNumber;

    @NotNull(message = "مبلغ انتقال الزامی است")
    @Positive(message = "مبلغ انتقال باید مثبت باشد")
    private BigDecimal amount;

    @Size(max = 200)
    private String description;

    public String getSourceCardNumber() {
        return sourceCardNumber;
    }

    public void setSourceCardNumber(String sourceCardNumber) {
        this.sourceCardNumber = sourceCardNumber;
    }

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
}
