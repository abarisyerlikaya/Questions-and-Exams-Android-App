package tr.edu.yildiz.ahmetbarisyerlikaya.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Question {
    private final int id;
    private String attachmentPath;
    private String infoText;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String optionE;
    private int correctAnswer;

    public Question(int id, String attachmentPath, String infoText, String questionText, String optionA, String optionB, String optionC, String optionD, String optionE, int correctAnswer) {
        this.id = id;
        this.attachmentPath = attachmentPath;
        this.infoText = infoText;
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.optionE = optionE;
        this.correctAnswer = correctAnswer;
    }

    public int getId() { return id; }

    public String getAttachmentPath() {
        return attachmentPath;
    }

    public void setAttachmentPath(String attachmentPath) {
        this.attachmentPath = attachmentPath;
    }

    public String getInfoText() {
        return infoText;
    }

    public void setInfoText(String infoText) {
        this.infoText = infoText;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getOptionE() {
        return optionE;
    }

    public void setOptionE(String optionE) {
        this.optionE = optionE;
    }

    public int getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(int correctAnswer) {
        this.correctAnswer = correctAnswer;
    }

    public ArrayList<String> getOptionsByDifficulty(int difficulty) {
        ArrayList<String> selectedOptions = null;
        List<String> allOptions = Arrays.asList(optionA, optionB, optionC, optionD, optionE);
        String correctOption = allOptions.get(correctAnswer);
        boolean containsCorrectAnswer = false;

        while (!containsCorrectAnswer) {
            Collections.shuffle(allOptions);
            selectedOptions = new ArrayList<>();

            for (int i = 0; i < difficulty; i++) selectedOptions.add(allOptions.get(i));
            containsCorrectAnswer = selectedOptions.contains(correctOption);
        }

        return selectedOptions;
    }
}