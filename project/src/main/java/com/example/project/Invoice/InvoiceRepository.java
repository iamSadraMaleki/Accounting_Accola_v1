package com.example.project.Invoice;


import com.example.project.signup.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {


    List<Invoice> findByIssuerOrderByIssueDateDesc(User issuer);
    List<Invoice> findByIssuer_IdOrderByIssueDateDesc(Long issuerId); // جایگزین بهتر


    Optional<Invoice> findByIdAndIssuer(Long id, User issuer);
    Optional<Invoice> findByIdAndIssuer_Id(Long id, Long issuerId); // جایگزین بهتر

    List<Invoice> findByRecipientNationalIdOrderByIssueDateDesc(String recipientNationalId);


    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
}
