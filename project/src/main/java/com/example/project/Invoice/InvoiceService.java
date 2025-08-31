package com.example.project.Invoice;


import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface InvoiceService {


    InvoiceDto createInvoice(CreateInvoiceRequest request);


    List<InvoiceDto> getInvoicesIssuedByCurrentUser();


    InvoiceDto getInvoiceByIdForIssuer(Long invoiceId);


    @Transactional(readOnly = true)
    List<InvoiceDto> getInvoicesReceivedByCurrentUser();


}