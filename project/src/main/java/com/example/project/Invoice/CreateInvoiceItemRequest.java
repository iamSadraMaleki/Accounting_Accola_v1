package com.example.project.Invoice;



import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class CreateInvoiceItemRequest {
    @NotBlank(message = "شرح کالا/خدمت الزامی است")
    private String description;

    @NotNull(message = "تعداد الزامی است")
    @Min(value = 1, message = "تعداد باید حداقل ۱ باشد") // حداقل ۱
    private Integer quantity; // یا BigDecimal

    @NotNull(message = "فی واحد الزامی است")
    @DecimalMin(value = "0.0", inclusive = true, message = "فی واحد نمی‌تواند منفی باشد")
    private BigDecimal unitPrice;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}
