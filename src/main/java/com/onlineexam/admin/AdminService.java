package com.onlineexam.admin;

import com.onlineexam.database.DatabaseConnection;
import com.onlineexam.models.Exam;
import com.onlineexam.models.Question;
import com.onlineexam.utils.InputValidator;

import java.sql.*;
import java.util.Scanner;

public class AdminService {

    private DatabaseConnection databaseConnection;

    public AdminService() {
        this.databaseConnection = new DatabaseConnection();
    }

    public void login(Scanner scanner) {
        System.out.print("Enter Admin Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        if (authenticateAdmin(username, password)) {
            System.out.println("✅ Admin login successful!");
            showAdminMenu(scanner);
        } else {
            System.err.println("❌ Invalid credentials. Access denied.");
        }
    }

    private boolean authenticateAdmin(String username, String password) {
        // Simple authentication - in production, use proper password hashing
        return "admin".equals(username) && "admin123".equals(password);
    }

    private void showAdminMenu(Scanner scanner) {
        while (true) {
            System.out.println("\n=== Admin Dashboard ===");
            System.out.println("1. Create Exam");
            System.out.println("2. Add Question to Exam");
            System.out.println("3. Assign Exam to Student");
            System.out.println("4. View Results");
            System.out.println("5. List All Exams");
            System.out.println("6. Logout");
            System.out.print("Choose option (1-6): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        handleCreateExam(scanner);
                        break;
                    case 2:
                        handleAddQuestion(scanner);
                        break;
                    case 3:
                        handleAssignExam(scanner);
                        break;
                    case 4:
                        handleViewResults(scanner);
                        break;
                    case 5:
                        listAllExams();
                        break;
                    case 6:
                        System.out.println("Admin logged out successfully.");
                        return;
                    default:
                        System.err.println("Invalid option. Please choose 1-6.");
                }
            } catch (Exception e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
            }
        }
    }

    private void handleCreateExam(Scanner scanner) {
        System.out.println("\n--- Create New Exam ---");
        
        System.out.print("Enter exam name: ");
        String name = scanner.nextLine().trim();
        
        if (!InputValidator.isNotEmpty(name)) {
            System.err.println("Exam name cannot be empty.");
            return;
        }

        System.out.print("Enter duration (minutes): ");
        int duration;
        try {
            duration = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Invalid duration. Please enter a number.");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter subject: ");
        String subject = scanner.nextLine().trim();

        // Create exam with basic info (timestamps can be set later)
        Exam exam = new Exam(0, name, duration, subject, null, null, false);
        createExam(exam);
    }

    private void handleAddQuestion(Scanner scanner) {
        System.out.println("\n--- Add Question to Exam ---");
        
        listAllExams();
        
        System.out.print("Enter Exam ID: ");
        int examId;
        try {
            examId = scanner.nextInt();
            scanner.nextLine();
        } catch (Exception e) {
            System.err.println("Invalid exam ID.");
            scanner.nextLine();
            return;
        }

        System.out.print("Enter question text: ");
        String questionText = scanner.nextLine().trim();

        System.out.print("Enter option A: ");
        String optionA = scanner.nextLine().trim();

        System.out.print("Enter option B: ");
        String optionB = scanner.nextLine().trim();

        System.out.print("Enter option C: ");
        String optionC = scanner.nextLine().trim();

        System.out.print("Enter option D: ");
        String optionD = scanner.nextLine().trim();

        System.out.print("Enter correct answer (A/B/C/D): ");
        String correctAnswer = scanner.nextLine().trim().toUpperCase();

        if (!correctAnswer.matches("[ABCD]")) {
            System.err.println("Correct answer must be A, B, C, or D.");
            return;
        }

        String[] options = {optionA, optionB, optionC, optionD};
        Question question = new Question(examId, questionText, options, correctAnswer);
        addQuestion(question);
    }

    private void handleAssignExam(Scanner scanner) {
        System.out.print("Enter Student ID: ");
        try {
            int studentId = scanner.nextInt();
            System.out.print("Enter Exam ID: ");
            int examId = scanner.nextInt();
            scanner.nextLine();
            
            assignExamToStudent(examId, studentId);
        } catch (Exception e) {
            System.err.println("Invalid input for student or exam ID.");
            scanner.nextLine();
        }
    }

    private void handleViewResults(Scanner scanner) {
        System.out.print("Enter Exam ID to view results: ");
        try {
            int examId = scanner.nextInt();
            scanner.nextLine();
            viewResults(examId);
        } catch (Exception e) {
            System.err.println("Invalid exam ID.");
            scanner.nextLine();
        }
    }

    public void createExam(Exam exam) {
        String query = "INSERT INTO exams (name, duration, subject, start_time, end_time) VALUES (?, ?, ?, SYSTIMESTAMP, SYSTIMESTAMP + INTERVAL '1' DAY)";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, exam.getName());
            ps.setInt(2, exam.getDuration());
            ps.setString(3, exam.getSubject());
            
            int rowsAffected = ps.executeUpdate();
            
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int examId = generatedKeys.getInt(1);
                        System.out.println("✅ Exam created successfully with ID: " + examId);
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error creating exam: " + e.getMessage());
        }
    }

    public void addQuestion(Question question) {
        String query = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, question.getExamId());
            ps.setString(2, question.getQuestionText());
            ps.setString(3, question.getOptions()[0]); // Option A
            ps.setString(4, question.getOptions()[1]); // Option B
            ps.setString(5, question.getOptions()[2]); // Option C
            ps.setString(6, question.getOptions()[3]); // Option D
            ps.setString(7, question.getCorrectAnswer());
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Question added successfully!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error adding question: " + e.getMessage());
        }
    }

    public void assignExamToStudent(int examId, int studentId) {
        String query = "INSERT INTO student_exams (student_id, exam_id) VALUES (?, ?)";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Exam assigned to student successfully!");
            }
            
        } catch (SQLException e) {
            if (e.getErrorCode() == 1) { // Duplicate key error
                System.err.println("❌ Exam already assigned to this student.");
            } else {
                System.err.println("❌ Error assigning exam: " + e.getMessage());
            }
        }
    }

    public void viewResults(int examId) {
        String query = "SELECT s.name, s.student_code, r.score, r.total_questions, r.completion_time " +
                      "FROM results r " +
                      "JOIN students s ON r.student_id = s.student_id " +
                      "WHERE r.exam_id = ? " +
                      "ORDER BY r.score DESC";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, examId);
            
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== Exam Results (ID: " + examId + ") ===");
                System.out.printf("%-20s %-15s %-10s %-15s %-20s%n", 
                    "Student Name", "Student Code", "Score", "Total Questions", "Completion Time");
                System.out.println("-".repeat(80));
                
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    System.out.printf("%-20s %-15s %-10d %-15d %-20s%n",
                        rs.getString("name"),
                        rs.getString("student_code"),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        rs.getTimestamp("completion_time"));
                }
                
                if (!hasResults) {
                    System.out.println("No results found for this exam.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error viewing results: " + e.getMessage());
        }
    }

    private void listAllExams() {
        String query = "SELECT exam_id, name, subject, duration FROM exams ORDER BY exam_id";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            System.out.println("\n=== Available Exams ===");
            System.out.printf("%-5s %-25s %-15s %-10s%n", "ID", "Name", "Subject", "Duration");
            System.out.println("-".repeat(55));
            
            boolean hasExams = false;
            while (rs.next()) {
                hasExams = true;
                System.out.printf("%-5d %-25s %-15s %-10d%n",
                    rs.getInt("exam_id"),
                    rs.getString("name"),
                    rs.getString("subject"),
                    rs.getInt("duration"));
            }
            
            if (!hasExams) {
                System.out.println("No exams found.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error listing exams: " + e.getMessage());
        }
    }
}