package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Loan;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.LoanRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminService {

	@Autowired
	private EmailService emailService;
	
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private LoanRepository loanRepository;

    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Get all accounts
    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    // Get all transactions
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    // Get all loans
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

 // Approve loan
    public Loan approveLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() ->
                new RuntimeException("Loan not found"));
        loan.setStatus(Loan.Status.APPROVED);
        loanRepository.save(loan);

        // Send email
        emailService.sendLoanApprovedEmail(
            loan.getUser().getEmail(),
            loan.getUser().getFullName(),
            loan.getLoanType().name(),
            loan.getAmount().doubleValue()
        );
        return loan;
    }

    // Reject loan
    public Loan rejectLoan(Long loanId) {
        Loan loan = loanRepository.findById(loanId)
            .orElseThrow(() ->
                new RuntimeException("Loan not found"));
        loan.setStatus(Loan.Status.REJECTED);
        loanRepository.save(loan);

        // Send email
        emailService.sendLoanRejectedEmail(
            loan.getUser().getEmail(),
            loan.getUser().getFullName(),
            loan.getLoanType().name(),
            loan.getAmount().doubleValue()
        );
        return loan;
    }

    // Block account
    public Account blockAccount(Long accountId) {
        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() ->
                new RuntimeException("Account not found"));
        account.setStatus(Account.Status.BLOCKED);
        return accountRepository.save(account);
    }

    // Unblock account
    public Account unblockAccount(Long accountId) {
        Account account = accountRepository
            .findById(accountId)
            .orElseThrow(() ->
                new RuntimeException("Account not found"));
        account.setStatus(Account.Status.ACTIVE);
        return accountRepository.save(account);
    }

    // Get dashboard statistics
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count stats
        stats.put("totalUsers",
            userRepository.count());
        stats.put("totalAccounts",
            accountRepository.count());
        stats.put("totalTransactions",
            transactionRepository.count());
        stats.put("totalLoans",
            loanRepository.count());

        // Pending loans
        List<Loan> allLoans = loanRepository.findAll();
        long pendingLoans = allLoans.stream()
            .filter(l -> l.getStatus() ==
                Loan.Status.PENDING)
            .count();
        stats.put("pendingLoans", pendingLoans);

        // Total balance across all accounts
        BigDecimal totalBalance = accountRepository
            .findAll()
            .stream()
            .map(Account::getBalance)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("totalBalance", totalBalance);

        return stats;
    }
}