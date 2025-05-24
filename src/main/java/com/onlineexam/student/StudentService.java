package com.onlineexam.student;

import com.onlineexam.database.DatabaseConnection;
import com.onlineexam.models.Exam;
import com.onlineexam.models.Question;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class StudentService {

    private DatabaseConnection databaseConnection;

    public StudentService() {
        this.databaseConnection = new DatabaseConnection();
    }

    public void takeExam(int studentId, int examId) {
        // Logic for a student to take an exam
        // Fetch exam details and questions from the database
    }

    public void submitAnswers(int studentId, int examId, List<String> answers) {
        // Logic for submitting answers
        // Save the answers to the database
    }

    public void viewScores(int studentId) {
        // Logic for viewing scores
        // Fetch scores from the database and display them
    }

    // Additional methods can be added as needed
}