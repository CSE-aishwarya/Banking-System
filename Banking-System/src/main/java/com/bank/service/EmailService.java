package com.bank.service;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

    public void sendWelcomeEmail(
            String to, String name) {
        System.out.println(
            "📧 Welcome email to: " + to);
    }

    public void sendDepositEmail(
            String to, String name,
            String accountNumber,
            double amount, double balance) {
        System.out.println(
            "📧 Deposit email to: " + to +
            " Amount: ₹" + amount);
    }

    public void sendWithdrawalEmail(
            String to, String name,
            String accountNumber,
            double amount, double balance) {
        System.out.println(
            "📧 Withdrawal email to: " + to +
            " Amount: ₹" + amount);
    }

    public void sendTransferSentEmail(
            String to, String name,
            String fromAccount,
            String toAccount,
            double amount, double balance) {
        System.out.println(
            "📧 Transfer sent email to: " + to +
            " Amount: ₹" + amount);
    }

    public void sendTransferReceivedEmail(
            String to, String name,
            String fromAccount,
            String toAccount,
            double amount, double balance) {
        System.out.println(
            "📧 Transfer received email to: "
            + to + " Amount: ₹" + amount);
    }

    public void sendLoanApplicationEmail(
            String to, String name,
            String loanType, double amount) {
        System.out.println(
            "📧 Loan application email to: " + to);
    }

    public void sendLoanApprovedEmail(
            String to, String name,
            String loanType, double amount) {
        System.out.println(
            "📧 Loan approved email to: " + to);
    }

    public void sendLoanRejectedEmail(
            String to, String name,
            String loanType, double amount) {
        System.out.println(
            "📧 Loan rejected email to: " + to);
    }
    public void sendPasswordResetEmail(
            String to, String name) {
        System.out.println(
            "📧 Password reset email to: " + to +
            " | Name: " + name);
    }
}