package com.onlineexam.student;

import com.onlineexam.database.DatabaseConnection;
import com.onlineexam.models.Question;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StudentService {

    private DatabaseConnection databaseConnection;

    public StudentService() {
        this.databaseConnection = new DatabaseConnection();
    }

    public void login(Scanner scanner) {
        System.out.print("Enter Student Email: ");
        String email = scanner.nextLine().trim();
        
        System.out.print("Enter Password: ");
        String password = scanner.nextLine().trim();

        Student student = authenticateStudent(email, password);
        if (student != null) {
            System.out.println("✅ Login successful! Welcome, " + student.getName());
            showStudentMenu(scanner, student);
        } else {
            System.err.println("❌ Invalid credentials or student not found.");
        }
    }

    private Student authenticateStudent(String email, String password) {
        String query = "SELECT student_id, student_code, name, email, password FROM students WHERE email = ?";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedPassword = rs.getString("password");
                    // In production, use proper password hashing (BCrypt, etc.)
                    if (password.equals(storedPassword)) {
                        return new Student(
                            rs.getInt("student_id"),
                            rs.getString("student_code"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("password")
                        );
                    }
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Database error during authentication: " + e.getMessage());
        }
        
        return null;
    }

    private void showStudentMenu(Scanner scanner, Student student) {
        while (true) {
            System.out.println("\n=== Student Dashboard ===");
            System.out.println("1. View Available Exams");
            System.out.println("2. Take Exam");
            System.out.println("3. View My Scores");
            System.out.println("4. Logout");
            System.out.print("Choose option (1-4): ");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        viewAvailableExams(student.getStudentId());
                        break;
                    case 2:
                        handleTakeExam(scanner, student);
                        break;
                    case 3:
                        viewScores(student.getStudentId());
                        break;
                    case 4:
                        System.out.println("Student logged out successfully.");
                        return;
                    default:
                        System.err.println("Invalid option. Please choose 1-4.");
                }
            } catch (Exception e) {
                System.err.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // clear invalid input
            }
        }
    }

    private void viewAvailableExams(int studentId) {
        String query = "SELECT e.exam_id, e.name, e.subject, e.duration " +
                      "FROM exams e " +
                      "JOIN student_exams se ON e.exam_id = se.exam_id " +
                      "WHERE se.student_id = ? " +
                      "AND e.exam_id NOT IN (SELECT exam_id FROM results WHERE student_id = ?)";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, studentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== Your Available Exams ===");
                System.out.printf("%-5s %-25s %-15s %-10s%n", "ID", "Name", "Subject", "Duration");
                System.out.println("-".repeat(55));
                
                boolean hasExams = false;
                while (rs.next()) {
                    hasExams = true;
                    System.out.printf("%-5d %-25s %-15s %-10d min%n",
                        rs.getInt("exam_id"),
                        rs.getString("name"),
                        rs.getString("subject"),
                        rs.getInt("duration"));
                }
                
                if (!hasExams) {
                    System.out.println("No available exams or all exams completed.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error viewing exams: " + e.getMessage());
        }
    }

    private void handleTakeExam(Scanner scanner, Student student) {
        viewAvailableExams(student.getStudentId());
        
        System.out.print("\nEnter Exam ID to take: ");
        try {
            int examId = scanner.nextInt();
            scanner.nextLine();
            takeExam(student.getStudentId(), examId, scanner);
        } catch (Exception e) {
            System.err.println("Invalid exam ID.");
            scanner.nextLine();
        }
    }

    public void takeExam(int studentId, int examId, Scanner scanner) {
        // Verify student is assigned to this exam
        if (!isStudentAssignedToExam(studentId, examId)) {
            System.err.println("❌ You are not assigned to this exam or have already completed it.");
            return;
        }

        List<Question> questions = getExamQuestions(examId);
        if (questions.isEmpty()) {
            System.err.println("❌ No questions found for this exam.");
            return;
        }

        System.out.println("\n=== Starting Exam ===");
        System.out.println("Total Questions: " + questions.size());
        System.out.println("Answer with A, B, C, or D");
        System.out.println("-".repeat(50));

        List<String> studentAnswers = new ArrayList<>();
        
        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            System.out.println("\nQuestion " + (i + 1) + ": " + q.getQuestionText());
            System.out.println("A) " + q.getOptions()[0]);
            System.out.println("B) " + q.getOptions()[1]);
            System.out.println("C) " + q.getOptions()[2]);
            System.out.println("D) " + q.getOptions()[3]);
            
            String answer;
            do {
                System.out.print("Your answer (A/B/C/D): ");
                answer = scanner.nextLine().trim().toUpperCase();
            } while (!answer.matches("[ABCD]"));
            
            studentAnswers.add(answer);
        }

        // Calculate score and submit
        int score = calculateScore(questions, studentAnswers);
        submitExamResult(studentId, examId, score, questions.size());
        
        System.out.println("\n=== Exam Completed ===");
        System.out.println("Your Score: " + score + "/" + questions.size());
        System.out.println("Percentage: " + String.format("%.1f", (score * 100.0 / questions.size())) + "%");
    }

    private boolean isStudentAssignedToExam(int studentId, int examId) {
        String query = "SELECT 1 FROM student_exams WHERE student_id = ? AND exam_id = ? " +
                      "AND exam_id NOT IN (SELECT exam_id FROM results WHERE student_id = ?)";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, studentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error checking exam assignment: " + e.getMessage());
            return false;
        }
    }

    private List<Question> getExamQuestions(int examId) {
        List<Question> questions = new ArrayList<>();
        String query = "SELECT question_id, question_text, option_a, option_b, option_c, option_d, correct_answer " +
                      "FROM questions WHERE exam_id = ? ORDER BY question_id";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, examId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String[] options = {
                        rs.getString("option_a"),
                        rs.getString("option_b"),
                        rs.getString("option_c"),
                        rs.getString("option_d")
                    };
                    
                    questions.add(new Question(
                        rs.getInt("question_id"),
                        examId,
                        rs.getString("question_text"),
                        options,
                        rs.getString("correct_answer")
                    ));
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching questions: " + e.getMessage());
        }
        
        return questions;
    }

    private int calculateScore(List<Question> questions, List<String> studentAnswers) {
        int score = 0;
        
        for (int i = 0; i < questions.size() && i < studentAnswers.size(); i++) {
            Question question = questions.get(i);
            String studentAnswer = studentAnswers.get(i);
            String correctAnswer = question.getCorrectAnswer();
            
            if (studentAnswer.equalsIgnoreCase(correctAnswer)) {
                score++;
            }
        }
        
        return score;
    }

    private void submitExamResult(int studentId, int examId, int score, int totalQuestions) {
        String insertQuery = "INSERT INTO results (student_id, exam_id, score, total_questions, percentage, exam_date) " +
                           "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(insertQuery)) {
            
            double percentage = (score * 100.0) / totalQuestions;
            
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, score);
            ps.setInt(4, totalQuestions);
            ps.setDouble(5, percentage);
            
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ Exam result saved successfully!");
            } else {
                System.err.println("❌ Failed to save exam result.");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error saving exam result: " + e.getMessage());
        }
    }

    private void viewScores(int studentId) {
        String query = "SELECT r.result_id, e.name as exam_name, e.subject, r.score, r.total_questions, " +
                      "r.percentage, r.exam_date " +
                      "FROM results r " +
                      "JOIN exams e ON r.exam_id = e.exam_id " +
                      "WHERE r.student_id = ? " +
                      "ORDER BY r.exam_date DESC";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                System.out.println("\n=== Your Exam Results ===");
                System.out.printf("%-25s %-15s %-8s %-8s %-12s %-20s%n", 
                    "Exam Name", "Subject", "Score", "Total", "Percentage", "Date");
                System.out.println("-".repeat(90));
                
                boolean hasResults = false;
                while (rs.next()) {
                    hasResults = true;
                    System.out.printf("%-25s %-15s %-8s %-8s %-12s %-20s%n",
                        truncateString(rs.getString("exam_name"), 24),
                        truncateString(rs.getString("subject"), 14),
                        rs.getInt("score"),
                        rs.getInt("total_questions"),
                        String.format("%.1f%%", rs.getDouble("percentage")),
                        rs.getTimestamp("exam_date").toString().substring(0, 19));
                }
                
                if (!hasResults) {
                    System.out.println("No exam results found. Take an exam to see your scores here.");
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error viewing scores: " + e.getMessage());
        }
    }

    private String truncateString(String str, int maxLength) {
        if (str == null) return "";
        if (str.length() <= maxLength) return str;
        return str.substring(0, maxLength - 3) + "...";
    }

    // Utility method to get student by ID (if needed by other parts of the system)
    public Student getStudentById(int studentId) {
        String query = "SELECT student_id, student_code, name, email, password FROM students WHERE student_id = ?";
        
        try (Connection connection = databaseConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, studentId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Student(
                        rs.getInt("student_id"),
                        rs.getString("student_code"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                    );
                }
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Error fetching student: " + e.getMessage());
        }