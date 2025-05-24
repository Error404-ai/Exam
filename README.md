# Online Exam and Assessment Platform

## Overview
The Online Exam and Assessment Platform is a Java-based console application designed to facilitate online examinations and assessments. It supports two user roles: Admin and Student, each with specific functionalities.

## Features
- **Admin Role:**
  - Log in to the system.
  - Create and manage exams.
  - Add questions to exams.
  - Assign exams to students.
  - View results of completed exams.

- **Student Role:**
  - Log in to the system.
  - Take assigned exams.
  - Submit answers for evaluation.
  - View scores after exam completion.

## Project Structure
```
OnlineExamPlatform
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── com
│   │   │   │   └── onlineexam
│   │   │   │       ├── admin
│   │   │   │       │   ├── Admin.java
│   │   │   │       │   └── AdminService.java
│   │   │   │       ├── student
│   │   │   │       │   ├── Student.java
│   │   │   │       │   └── StudentService.java
│   │   │   │       ├── database
│   │   │   │       │   └── DatabaseConnection.java
│   │   │   │       ├── models
│   │   │   │       │   ├── Exam.java
│   │   │   │       │   └── Question.java
│   │   │   │       ├── utils
│   │   │   │       │   └── InputValidator.java
│   │   │   │       └── Main.java
│   │   └── resources
│   │       └── application.properties
├── lib
│   └── ojdbc8.jar
├── README.md
└── pom.xml
```

## Setup Instructions
1. **Prerequisites:**
   - Java Development Kit (JDK 8 or above)
   - Oracle Database
   - Maven for dependency management

2. **Clone the Repository:**
   ```
   git clone <repository-url>
   cd OnlineExamPlatform
   ```

3. **Configure Database:**
   - Update the `application.properties` file with your Oracle Database connection details.

4. **Build the Project:**
   ```
   mvn clean install
   ```

5. **Run the Application:**
   ```
   mvn exec:java -Dexec.mainClass="com.onlineexam.Main"
   ```

## Usage
- Upon running the application, users will be prompted to log in as either Admin or Student.
- Admins can create exams and manage questions, while Students can take exams and view their scores.

## Conclusion
This platform aims to streamline the examination process, making it efficient and accessible for both administrators and students.