package com.bank.controller;

import com.bank.model.Account;
import com.bank.model.Loan;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    // Dashboard stats
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(
            adminService.getDashboardStats());
    }

    // Get all users
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(
            adminService.getAllUsers());
    }

    // Get all accounts
    @GetMapping("/accounts")
    public ResponseEntity<List<Account>> getAllAccounts() {
        return ResponseEntity.ok(
            adminService.getAllAccounts());
    }

    // Get all transactions
    @GetMapping("/transactions")
    public ResponseEntity<List<Transaction>>
            getAllTransactions() {
        return ResponseEntity.ok(
            adminService.getAllTransactions());
    }

    // Get all loans
    @GetMapping("/loans")
    public ResponseEntity<List<Loan>> getAllLoans() {
        return ResponseEntity.ok(
            adminService.getAllLoans());
    }

    // Approve loan
    @PutMapping("/loans/{id}/approve")
    public ResponseEntity<?> approveLoan(
            @PathVariable Long id) {
        try {
            Loan loan = adminService.approveLoan(id);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Reject loan
    @PutMapping("/loans/{id}/reject")
    public ResponseEntity<?> rejectLoan(
            @PathVariable Long id) {
        try {
            Loan loan = adminService.rejectLoan(id);
            return ResponseEntity.ok(loan);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Block account
    @PutMapping("/accounts/{id}/block")
    public ResponseEntity<?> blockAccount(
            @PathVariable Long id) {
        try {
            Account acc = adminService.blockAccount(id);
            return ResponseEntity.ok(acc);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }

    // Unblock account
    @PutMapping("/accounts/{id}/unblock")
    public ResponseEntity<?> unblockAccount(
            @PathVariable Long id) {
        try {
            Account acc = adminService.unblockAccount(id);
            return ResponseEntity.ok(acc);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(e.getMessage());
        }
    }
}