package com.example.project.Invoice;


import com.example.project.cardmanagment.ResourceNotFoundException;
import com.example.project.dashboard.UserProfile;
import com.example.project.signup.User;
import com.example.project.signup.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize; // <<< امنیت در سطح متد
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    private static final Logger log = LoggerFactory.getLogger(InvoiceServiceImpl.class);

    @Autowired private InvoiceRepository invoiceRepository;
    @Autowired private UserRepository userRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("کاربر یافت نشد: " + username));
    }


    private InvoiceItemDto mapToItemDto(InvoiceItem item) {
        if (item == null) return null;
        InvoiceItemDto dto = new InvoiceItemDto();
        dto.setId(item.getId());
        dto.setDescription(item.getDescription());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setTotalPrice(item.getTotalPrice());
        return dto;
    }

    private InvoiceDto mapToInvoiceDto(Invoice invoice) {
        if (invoice == null) return null;
        InvoiceDto dto = new InvoiceDto();
        dto.setId(invoice.getId());
        dto.setInvoiceNumber(invoice.getInvoiceNumber());
        dto.setIssueDate(invoice.getIssueDate());
        dto.setDueDate(invoice.getDueDate());
        dto.setStatus(invoice.getStatus());
        dto.setRecipientName(invoice.getRecipientName());
        dto.setRecipientPhone(invoice.getRecipientPhone());
        dto.setRecipientNationalId(invoice.getRecipientNationalId());
        dto.setNotes(invoice.getNotes());
        dto.setSubTotal(invoice.getSubTotal());
        dto.setTaxAmount(invoice.getTaxAmount());
        dto.setTotalAmount(invoice.getTotalAmount());
        dto.setAmountPaid(invoice.getAmountPaid());

        dto.setAmountDue(invoice.getTotalAmount().subtract(invoice.getAmountPaid()));
        dto.setCreatedAt(invoice.getCreatedAt());
        dto.setUpdatedAt(invoice.getUpdatedAt());

        if (invoice.getIssuer() != null) {
            dto.setIssuerUsername(invoice.getIssuer().getUsername());
            dto.setIssuerBusinessName(invoice.getIssuer().getBusinessName()); // از فیلد User
        }
        if (invoice.getItems() != null) {
            dto.setItems(invoice.getItems().stream().map(this::mapToItemDto).collect(Collectors.toList()));
        }
        return dto;
    }


    @Override
    @Transactional // <<< مهم: عملیات ایجاد فاکتور باید کامل انجام شود
    @PreAuthorize("hasRole('BUSINESS')") // <<< فقط کاربر با نقش BUSINESS می‌تواند فاکتور بسازد
    public InvoiceDto createInvoice(CreateInvoiceRequest request) {
        User issuer = getCurrentUser();
        log.info("User {} with role {} attempting to create invoice", issuer.getUsername(), issuer.getRole());



        Invoice invoice = new Invoice();
        invoice.setIssuer(issuer);
        invoice.setRecipientName(request.getRecipientName());
        invoice.setRecipientPhone(request.getRecipientPhone());
        invoice.setRecipientNationalId(request.getRecipientNationalId());
        invoice.setNotes(request.getNotes());
        invoice.setIssueDate(request.getIssueDate() != null ? request.getIssueDate() : LocalDate.now());
        invoice.setDueDate(request.getDueDate());
        invoice.setStatus(Invoice.InvoiceStatus.ISSUED);


        invoice.setInvoiceNumber("INV-" + System.currentTimeMillis()); // یا UUID.randomUUID().toString()


        for (CreateInvoiceItemRequest itemRequest : request.getItems()) {
            InvoiceItem item = new InvoiceItem(
                    itemRequest.getDescription(),
                    itemRequest.getQuantity(),
                    itemRequest.getUnitPrice()
            );

            invoice.addItem(item);
        }


        invoice.calculateTotals();


        Invoice savedInvoice = invoiceRepository.save(invoice);
        log.info("Invoice {} created successfully for user {}", savedInvoice.getInvoiceNumber(), issuer.getUsername());

        return mapToInvoiceDto(savedInvoice);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('BUSINESS')")
    public List<InvoiceDto> getInvoicesIssuedByCurrentUser() {
        User currentUser = getCurrentUser();
        log.info("Fetching invoices issued by user: {}", currentUser.getUsername());
        List<Invoice> invoices = invoiceRepository.findByIssuer_IdOrderByIssueDateDesc(currentUser.getId());
        return invoices.stream().map(this::mapToInvoiceDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('BUSINESS')")
    public InvoiceDto getInvoiceByIdForIssuer(Long invoiceId) {
        User currentUser = getCurrentUser();
        log.info("Fetching invoice ID: {} for issuer: {}", invoiceId, currentUser.getUsername());
        Invoice invoice = invoiceRepository.findByIdAndIssuer_Id(invoiceId, currentUser.getId())
                .orElseThrow(() -> new ResourceNotFoundException("فاکتور با شناسه " + invoiceId + " یافت نشد یا متعلق به شما نیست."));
        return mapToInvoiceDto(invoice);
    }
    @Override
    @Transactional(readOnly = true)
    public List<InvoiceDto> getInvoicesReceivedByCurrentUser() {
        User currentUser = getCurrentUser();
        log.info("Fetching received invoices for user: {}", currentUser.getUsername());


        UserProfile userProfile = currentUser.getUserProfile();
        if (userProfile == null || userProfile.getNationalId() == null || userProfile.getNationalId().isEmpty()) {
            log.warn("User {} has no profile or national ID set. Cannot fetch received invoices.", currentUser.getUsername());

            return Collections.emptyList();
        }

        String userNationalId = userProfile.getNationalId();
        log.debug("User national ID: {}", userNationalId);


        List<Invoice> invoices = invoiceRepository.findByRecipientNationalIdOrderByIssueDateDesc(userNationalId);
        log.info("Found {} received invoices for user {}", invoices.size(), currentUser.getUsername());


        return invoices.stream().map(this::mapToInvoiceDto).collect(Collectors.toList());
    }
}
