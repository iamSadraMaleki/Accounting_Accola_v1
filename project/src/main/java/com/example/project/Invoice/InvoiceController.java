package com.example.project.Invoice;



import com.example.project.cardmanagment.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/invoices")

public class InvoiceController {

    private static final Logger log = LoggerFactory.getLogger(InvoiceController.class);

    @Autowired
    private InvoiceService invoiceService;


    @PostMapping
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<?> createNewInvoice(@Valid @RequestBody CreateInvoiceRequest request) {
        log.info("Request received to create a new invoice");
        try {
            InvoiceDto createdInvoice = invoiceService.createInvoice(request);

            return ResponseEntity.status(HttpStatus.CREATED).body(createdInvoice);
        } catch (IllegalArgumentException e) {

            log.warn("Invoice creation failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            log.error("Error creating invoice", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور هنگام ایجاد فاکتور."));
        }
    }


    @GetMapping
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<List<InvoiceDto>> getMyIssuedInvoices() {
        log.info("Request received to get issued invoices for current user");
        try {
            List<InvoiceDto> invoices = invoiceService.getInvoicesIssuedByCurrentUser();
            return ResponseEntity.ok(invoices);
        } catch (Exception e) {
            log.error("Error fetching issued invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }


    @GetMapping("/{invoiceId}")
    @PreAuthorize("hasRole('BUSINESS')")
    public ResponseEntity<?> getMyIssuedInvoiceById(@PathVariable Long invoiceId) {
        log.info("Request received to get issued invoice with ID: {}", invoiceId);
        try {
            InvoiceDto invoice = invoiceService.getInvoiceByIdForIssuer(invoiceId);
            return ResponseEntity.ok(invoice);
        } catch (ResourceNotFoundException e) {
            log.warn("Invoice lookup failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (AccessDeniedException e) {
            log.warn("Access denied for invoice ID {}: {}", invoiceId, e.getMessage());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "شما اجازه دسترسی به این فاکتور را ندارید."));
        } catch (Exception e) {
            log.error("Error fetching invoice by ID: {}", invoiceId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "خطای داخلی سرور."));
        }
    }

    @GetMapping("/received")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<InvoiceDto>> getMyReceivedInvoices() {
        log.info("Request received to get received invoices for current user");
        try {
            List<InvoiceDto> invoices = invoiceService.getInvoicesReceivedByCurrentUser();
            return ResponseEntity.ok(invoices);
        } catch (ResourceNotFoundException e) {
            log.warn("Cannot fetch received invoices: {}", e.getMessage());
            return ResponseEntity.ok(Collections.emptyList());
        } catch (Exception e) {
            log.error("Error fetching received invoices", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.emptyList());
        }
    }

}
