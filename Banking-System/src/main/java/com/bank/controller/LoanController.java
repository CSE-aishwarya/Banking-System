package com.bank.controller;

import com.bank.dto.LoanRequest;
import com.bank.model.Loan;
import com.bank.model.User;
import com.bank.repository.LoanRepository;
import com.bank.repository.UserRepository;
import com.bank.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/loans")
@CrossOrigin(origins = "*")
public class LoanController {

    @Autowired
    private LoanRepository loanRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyLoan(
            Authentication auth,
            @RequestBody LoanRequest request) {
        try {
            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException("User not found"));

            Loan loan = new Loan();
            loan.setUser(user);
            loan.setLoanType(Loan.LoanType
                .valueOf(request.getLoanType()));
            loan.setAmount(request.getAmount());
            loan.setInterestRate(new BigDecimal("8.5"));
            loan.setTenureMonths(request.getTenureMonths());
            loan.setStatus(Loan.Status.PENDING);
            loanRepository.save(loan);

            // Send email notification
            emailService.sendLoanApplicationEmail(
                user.getEmail(),
                user.getFullName(),
                request.getLoanType(),
                request.getAmount().doubleValue()
            );

            return ResponseEntity.ok(
                "Loan application submitted!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/my-loans")
    public ResponseEntity<?> getMyLoans(
            Authentication auth) {
        try {
            User user = userRepository
                .findByEmail(auth.getName())
                .orElseThrow(() ->
                    new RuntimeException("User not found"));
            List<Loan> loans =
                loanRepository.findByUser(user);
            return ResponseEntity.ok(loans);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}