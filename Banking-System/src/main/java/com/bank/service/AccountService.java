package com.bank.service;

import com.bank.model.Account;
import com.bank.model.Transaction;
import com.bank.model.User;
import com.bank.repository.AccountRepository;
import com.bank.repository.TransactionRepository;
import com.bank.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public Account createAccount(
            String email,
            Account.AccountType type) {
        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));
        Account account = new Account();
        account.setUser(user);
        account.setAccountType(type);
        account.setAccountNumber(generateAccountNumber());
        account.setBalance(BigDecimal.ZERO);
        account.setStatus(Account.Status.ACTIVE);
        return accountRepository.save(account);
    }

    public List<Account> getUserAccounts(String email) {
        User user = userRepository
            .findByEmail(email)
            .orElseThrow(() ->
                new RuntimeException("User not found"));
        return accountRepository.findByUser(user);
    }

    @Transactional
    public Account deposit(String accountNumber,
                            BigDecimal amount) {
        Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() ->
                new RuntimeException("Account not found"));

        if (account.getStatus() == Account.Status.BLOCKED)
            throw new RuntimeException(
                "Account is blocked!");

        account.setBalance(
            account.getBalance().add(amount));
        accountRepository.save(account);

        saveTransaction(null, accountNumber,
            Transaction.TransactionType.DEPOSIT,
            amount, "Deposit");

        // Send email
        emailService.sendDepositEmail(
            account.getUser().getEmail(),
            account.getUser().getFullName(),
            accountNumber,
            amount.doubleValue(),
            account.getBalance().doubleValue()
        );

        return account;
    }

    @Transactional
    public Account withdraw(String accountNumber,
                             BigDecimal amount) {
        Account account = accountRepository
            .findByAccountNumber(accountNumber)
            .orElseThrow(() ->
                new RuntimeException("Account not found"));

        if (account.getStatus() == Account.Status.BLOCKED)
            throw new RuntimeException(
                "Account is blocked!");

        if (account.getBalance().compareTo(amount) < 0)
            throw new RuntimeException(
                "Insufficient balance!");

        account.setBalance(
            account.getBalance().subtract(amount));
        accountRepository.save(account);

        saveTransaction(accountNumber, null,
            Transaction.TransactionType.WITHDRAWAL,
            amount, "Withdrawal");

        // Send email
        emailService.sendWithdrawalEmail(
            account.getUser().getEmail(),
            account.getUser().getFullName(),
            accountNumber,
            amount.doubleValue(),
            account.getBalance().doubleValue()
        );

        return account;
    }

    @Transactional
    public void transfer(String fromAcc,
                          String toAcc,
                          BigDecimal amount) {
        Account from = accountRepository
            .findByAccountNumber(fromAcc)
            .orElseThrow(() ->
                new RuntimeException(
                    "Source account not found"));

        Account to = accountRepository
            .findByAccountNumber(toAcc)
            .orElseThrow(() ->
                new RuntimeException(
                    "Destination account not found"));

        if (from.getStatus() == Account.Status.BLOCKED)
            throw new RuntimeException(
                "Your account is blocked!");

        if (to.getStatus() == Account.Status.BLOCKED)
            throw new RuntimeException(
                "Recipient account is blocked!");

        if (from.getBalance().compareTo(amount) < 0)
            throw new RuntimeException(
                "Insufficient balance!");

        from.setBalance(
            from.getBalance().subtract(amount));
        to.setBalance(
            to.getBalance().add(amount));

        accountRepository.save(from);
        accountRepository.save(to);

        saveTransaction(fromAcc, toAcc,
            Transaction.TransactionType.TRANSFER,
            amount, "Fund Transfer");

        // Send email to sender
        emailService.sendTransferSentEmail(
            from.getUser().getEmail(),
            from.getUser().getFullName(),
            fromAcc, toAcc,
            amount.doubleValue(),
            from.getBalance().doubleValue()
        );

        // Send email to receiver
        emailService.sendTransferReceivedEmail(
            to.getUser().getEmail(),
            to.getUser().getFullName(),
            fromAcc, toAcc,
            amount.doubleValue(),
            to.getBalance().doubleValue()
        );
    }

    private void saveTransaction(
            String from, String to,
            Transaction.TransactionType type,
            BigDecimal amount, String desc) {
        Transaction tx = new Transaction();
        tx.setTransactionId(UUID.randomUUID().toString());
        tx.setFromAccount(from);
        tx.setToAccount(to);
        tx.setTransactionType(type);
        tx.setAmount(amount);
        tx.setDescription(desc);
        tx.setStatus(Transaction.Status.SUCCESS);
        transactionRepository.save(tx);
    }

    private String generateAccountNumber() {
        return "ACC" + (100000000 +
            new Random().nextInt(900000000));
    }
}