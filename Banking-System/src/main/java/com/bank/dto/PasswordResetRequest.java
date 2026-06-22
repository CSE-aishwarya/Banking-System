package com.bank.dto;

public class PasswordResetRequest {
    private String email;
    private String newPassword;
    private String confirmPassword;

    public String getEmail() { return email; }
    public String getNewPassword() { return newPassword; }
    public String getConfirmPassword() {
        return confirmPassword; }
    public void setEmail(String email) {
        this.email = email; }
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword; }
    public void setConfirmPassword(
            String confirmPassword) {
        this.confirmPassword = confirmPassword; }
}