package com.onlineexam.admin;

public class Admin {
    private String adminId;
    private String password;

    public Admin(String adminId, String password) {
        this.adminId = adminId;
        this.password = password;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean login(String adminId, String password) {
        // Logic for admin login
        return this.adminId.equals(adminId) && this.password.equals(password);
    }

    public void createExam() {
        // Logic for creating an exam
    }

    public void viewResults() {
        // Logic for viewing results
    }
}