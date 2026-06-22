package com.bank.controller;

import com.bank.dto.TransferRequest;
import com.bank.model.Account;
import com.bank.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping("/create")
    public ResponseEntity<?> createAccount(
            Authentication auth,
            @RequestParam String accountType) {
        try {
            Account account = accountService.createAccount(
                auth.getName(),
                Account.AccountType.valueOf(accountType));
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @GetMapping("/my-accounts")
    public ResponseEntity<?> getMyAccounts(
            Authentication auth) {
        try {
            List<Account> accounts = accountService
                .getUserAccounts(auth.getName());
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<?> deposit(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        try {
            Account account = accountService
                .deposit(accountNumber, amount);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<?> withdraw(
            @RequestParam String accountNumber,
            @RequestParam BigDecimal amount) {
        try {
            Account account = accountService
                .withdraw(accountNumber, amount);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(
            @RequestBody TransferRequest request) {
        try {
            accountService.transfer(
                request.getFromAccount(),
                request.getToAccount(),
                request.getAmount());
            return ResponseEntity.ok("Transfer successful!");
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}