package com.bank.controller;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import com.bank.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/statement")
@CrossOrigin(origins = "*")
public class StatementController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private PdfService pdfService;

    @GetMapping("/download")
    public ResponseEntity<byte[]> downloadStatement(
            Authentication auth) {
        try {
            // Get user
            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException(
                        "User not found"));

            // Get accounts
            List<Account> accounts =
                accountRepository.findByUser(user);

            // Get all transactions
            List<Transaction> transactions =
                transactionRepository.findAll();

            // Generate PDF
            byte[] pdf =
                pdfService.generateBankStatement(
                    user, accounts, transactions);

            // Return PDF file
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(
                MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData(
                "attachment",
                "SecureBank_Statement_" +
                user.getFullName()
                .replaceAll(" ", "_") + ".pdf");
            headers.setContentLength(pdf.length);

            return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .build();
        }
    }
}