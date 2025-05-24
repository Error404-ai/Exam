package com.onlineexam;

import com.onlineexam.admin.AdminService;
import com.onlineexam.student.StudentService;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Online Exam and Assessment Platform");
        System.out.println("1. Admin Login");
        System.out.println("2. Student Login");
        System.out.print("Please select your role (1 or 2): ");
        
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                AdminService adminService = new AdminService();
                adminService.login(scanner);
                break;
            case 2:
                StudentService studentService = new StudentService();
                studentService.login(scanner);
                break;
            default:
                System.out.println("Invalid choice. Please restart the application.");
        }

        scanner.close();
    }
}