package com.example.project.Invoice;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class CreateInvoiceRequest {

    @NotBlank(message = "نام گیرنده فاکتور الزامی است")
    @Size(max = 200)
    private String recipientName;

    @Size(max = 20)
    private String recipientPhone;

    @Size(max = 10)
    @Pattern(regexp = "\\d{10}", message="کد ملی گیرنده باید ۱۰ رقم باشد")
    private String recipientNationalId;

    private LocalDate issueDate;
    private LocalDate dueDate;

    @Size(max = 1000)
    private String notes;

    @Valid // << اعتبارسنجی آیتم‌های داخل لیست
    @NotEmpty(message = "فاکتور باید حداقل یک آیتم داشته باشد")
    @NotNull
    private List<CreateInvoiceItemRequest> items;

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    public String getRecipientNationalId() {
        return recipientNationalId;
    }

    public void setRecipientNationalId(String recipientNationalId) {
        this.recipientNationalId = recipientNationalId;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public List<CreateInvoiceItemRequest> getItems() {
        return items;
    }

    public void setItems(List<CreateInvoiceItemRequest> items) {
        this.items = items;
    }
}
