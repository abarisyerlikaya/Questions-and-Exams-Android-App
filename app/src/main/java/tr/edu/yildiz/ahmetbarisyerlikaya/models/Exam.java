package tr.edu.yildiz.ahmetbarisyerlikaya.models;

import java.util.ArrayList;
import java.util.List;

public class Exam {
    private final List<Question> questions;
    private String examName;
    private int examTime; // In minutes

    public Exam(String examName, int examTime) {
        this.examName = examName;
        this.examTime = examTime;
        this.questions = new ArrayList<Question>();
    }

    public void addQuestion(Question question) { questions.add(question); }

    public void removeQuestion(int index) { questions.remove(index); }

    public String getExamName() { return examName; }

    public void setExamName(String examName) { this.examName = examName; }

    public int getExamTime() { return examTime; }

    public void setExamTime(int examTime) { this.examTime = examTime; }
}
