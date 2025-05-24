package com.onlineexam.student;

public class Student {
    private int studentId;
    private String studentCode;
    private String name;
    private String email;
    private String password;

    public Student(String studentCode, String name, String email, String password) {
        this.studentCode = studentCode;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Student(int studentId, String studentCode, String name, String email, String password) {
        this.studentId = studentId;
        this.studentCode = studentCode;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean login(String email, String password) {
        return this.email.equals(email) && this.password.equals(password);
    }

    public void viewScores() {
        System.out.println("Viewing scores for student: " + this.name);
    }
}
