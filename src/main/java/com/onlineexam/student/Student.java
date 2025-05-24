public class Student {
    private String studentId;
    private String name;
    private String email;

    public Student(String studentId, String name, String email) {
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
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

    public boolean login(String email, String password) {
        // Logic for student login
        return true; // Placeholder for actual login logic
    }

    public void viewScores() {
        // Logic to view scores
    }
}