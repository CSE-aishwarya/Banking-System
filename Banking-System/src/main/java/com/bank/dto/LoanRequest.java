package com.bank.dto;

import java.math.BigDecimal;

public class LoanRequest {
    private String loanType;
    private BigDecimal amount;
    private Integer tenureMonths;

    public String getLoanType() { return loanType; }
    public BigDecimal getAmount() { return amount; }
    public Integer getTenureMonths() { return tenureMonths; }
    public void setLoanType(String loanType) { this.loanType = loanType; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setTenureMonths(Integer tenureMonths) {
        this.tenureMonths = tenureMonths; }
}