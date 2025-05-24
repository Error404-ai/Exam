package com.onlineexam.admin;

import com.onlineexam.database.DatabaseConnection;
import com.onlineexam.models.Exam;
import com.onlineexam.models.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminService {

    private DatabaseConnection databaseConnection;

    public AdminService() {
        this.databaseConnection = new DatabaseConnection();
    }

    public void createExam(Exam exam) {
        String query = "INSERT INTO exams (name, duration, subject, start_time, end_time) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, exam.getName());
            preparedStatement.setInt(2, exam.getDuration());
            preparedStatement.setString(3, exam.getSubject());
            preparedStatement.setTimestamp(4, exam.getStartTime());
            preparedStatement.setTimestamp(5, exam.getEndTime());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addQuestion(Question question) {
        String query = "INSERT INTO questions (exam_id, question_text, options, correct_answer) VALUES (?, ?, ?, ?)";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, question.getExamId());
            preparedStatement.setString(2, question.getQuestionText());
            preparedStatement.setArray(3, connection.createArrayOf("varchar", question.getOptions()));
            preparedStatement.setString(4, question.getCorrectAnswer());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void assignExamToStudent(int examId, int studentId) {
        String query = "INSERT INTO student_exams (student_id, exam_id) VALUES (?, ?)";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, studentId);
            preparedStatement.setInt(2, examId);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void viewResults(int examId) {
        String query = "SELECT * FROM results WHERE exam_id = ?";
        try (Connection connection = databaseConnection.connect();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, examId);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                System.out.println("Student ID: " + resultSet.getInt("student_id") + 
                                   ", Score: " + resultSet.getInt("score"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}