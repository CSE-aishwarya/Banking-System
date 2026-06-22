package com.bank.dto;

import java.math.BigDecimal;

public class TransferRequest {
    private String fromAccount;
    private String toAccount;
    private BigDecimal amount;

    public String getFromAccount() { return fromAccount; }
    public String getToAccount() { return toAccount; }
    public BigDecimal getAmount() { return amount; }
    public void setFromAccount(String fromAccount) {
        this.fromAccount = fromAccount; }
    public void setToAccount(String toAccount) {
        this.toAccount = toAccount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
}