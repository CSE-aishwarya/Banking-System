package com.bank.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
public class Loan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "loan_type")
    private LoanType loanType;

    private BigDecimal amount;

    @Column(name = "interest_rate")
    private BigDecimal interestRate;

    @Column(name = "tenure_months")
    private Integer tenureMonths;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public enum LoanType {
        HOME, CAR, PERSONAL, EDUCATION
    }

    public enum Status {
        PENDING, APPROVED, REJECTED, CLOSED
    }

    // Getters
    public Long getId() { return id; }
    public User getUser() { return user; }
    public LoanType getLoanType() { return loanType; }
    public BigDecimal getAmount() { return amount; }
    public BigDecimal getInterestRate() { return interestRate; }
    public Integer getTenureMonths() { return tenureMonths; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setUser(User user) { this.user = user; }
    public void setLoanType(LoanType loanType) {
        this.loanType = loanType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate; }
    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths; }
    public void setStatus(Status status) { this.status = status; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; }
}