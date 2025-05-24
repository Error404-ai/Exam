package com.onlineexam.utils;

import java.util.regex.Pattern;

public class InputValidator {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";

    public static boolean isValidEmail(String email) {
        return email != null && Pattern.matches(EMAIL_REGEX, email);
    }

    public static boolean isNotEmpty(String input) {
        return input != null && !input.trim().isEmpty();
    }

    public static boolean isValidStudentId(String studentId) {
        return isNotEmpty(studentId) && studentId.matches("\\d+");
    }

    public static boolean isValidExamId(String examId) {
        return isNotEmpty(examId) && examId.matches("\\d+");
    }
}