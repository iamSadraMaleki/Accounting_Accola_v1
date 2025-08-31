package com.example.project.Invoice;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "invoice_items")
@Data
public class InvoiceItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "شرح کالا/خدمات الزامی است")
    @Column(nullable = false)
    private String description; // نام یا شرح کالا/خدمت

    @NotNull(message = "تعداد الزامی است")
    @Positive(message = "تعداد باید مثبت باشد") // یا PositiveOrZero اگر صفر مجاز است
    @Column(nullable = false)
    private Integer quantity = 1; // تعداد (پیش‌فرض ۱)

    @NotNull(message = "فی واحد الزامی است")
    @PositiveOrZero(message = "فی واحد نمی‌تواند منفی باشد") // قیمت می‌تواند صفر باشد
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal unitPrice = BigDecimal.ZERO; // فی هر واحد

    @NotNull
    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal totalPrice = BigDecimal.ZERO; // قیمت کل این ردیف (تعداد * فی)

    // --- ارتباط با فاکتور مادر ---
    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "invoice_id", nullable = false)
    private Invoice invoice;
    // --------------------------


    public InvoiceItem(String description, Integer quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity != null && quantity > 0 ? quantity : 1;
        this.unitPrice = unitPrice != null ? unitPrice : BigDecimal.ZERO;
        this.calculateTotalPrice();
    }


    public void calculateTotalPrice() {
        if (this.quantity == null || this.unitPrice == null) {
            this.totalPrice = BigDecimal.ZERO;
        } else {
            this.totalPrice = this.unitPrice.multiply(new BigDecimal(this.quantity));
        }
    }



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
    public InvoiceItem() {}
}
