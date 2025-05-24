package com.onlineexam.models;

import java.time.LocalDateTime;

public class Exam {
    private int examId;
    private String name;
    private int duration; // in minutes
    private String subject;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean randomizeQuestions;

    public Exam(int examId, String name, int duration, String subject, LocalDateTime startTime, LocalDateTime endTime, boolean randomizeQuestions) {
        this.examId = examId;
        this.name = name;
        this.duration = duration;
        this.subject = subject;
        this.startTime = startTime;
        this.endTime = endTime;
        this.randomizeQuestions = randomizeQuestions;
    }

    public int getExamId() {
        return examId;
    }

    public void setExamId(int examId) {
        this.examId = examId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public boolean isRandomizeQuestions() {
        return randomizeQuestions;
    }

    public void setRandomizeQuestions(boolean randomizeQuestions) {
        this.randomizeQuestions = randomizeQuestions;
    }
}