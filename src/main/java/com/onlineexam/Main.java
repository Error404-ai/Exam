package com.onlineexam;

import com.onlineexam.admin.AdminService;
import com.onlineexam.student.StudentService;
import com.onlineexam.database.DatabaseConnection;

import java.util.Scanner;
import java.util.InputMismatchException;

public class Main {
    public static void main(String[] args) {
        // Test database connection first
        DatabaseConnection dbTest = new DatabaseConnection();
        if (!dbTest.testConnection()) {
            System.err.println("Cannot connect to database. Please check your Oracle setup.");
            System.err.println("Make sure Oracle is running and credentials are correct.");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        
        try {
            System.out.println("=================================");
            System.out.println("Online Exam and Assessment Platform");
            System.out.println("=================================");
            System.out.println("1. Admin Login");
            System.out.println("2. Student Login");
            System.out.println("3. Exit");
            System.out.print("Please select your role (1-3): ");

            int choice = 0;
            try {
                choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number between 1-3.");
                scanner.nextLine(); // Clear invalid input
                return;
            }

            switch (choice) {
                case 1:
                    System.out.println("\n--- Admin Login ---");
                    AdminService adminService = new AdminService();
                    adminService.login(scanner);
                    break;
                case 2:
                    System.out.println("\n--- Student Login ---");
                    StudentService studentService = new StudentService();
                    studentService.login(scanner);
                    break;
                case 3:
                    System.out.println("Thank you for using Online Exam Platform. Goodbye!");
                    break;
                default:
                    System.err.println("Invalid choice. Please select 1, 2, or 3.");
            }

        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}