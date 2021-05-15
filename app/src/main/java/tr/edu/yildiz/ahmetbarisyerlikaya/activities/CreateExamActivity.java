package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.models.Question;

import static android.content.Intent.ACTION_SEND;
import static android.content.Intent.EXTRA_STREAM;
import static android.os.Environment.getExternalStorageDirectory;
import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.parseInt;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.DEFAULT_DIFFICULTY;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.DEFAULT_EXAM_LENGTH;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.DEFAULT_POINTS_PER_QUESTION;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.generateTimestamp;

public class CreateExamActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 4000;
    private Button selectQuestionsButton;
    private EditText examLengthTextField;
    private EditText pointsTextField;
    private TextView difficultyLabel;
    private SeekBar difficultySlider;
    private ArrayList<Integer> selectedQuestionIds;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_exam);
        initialize();
    }

    public void initialize() {
        userId = getIntent().getStringExtra("userId");
        selectedQuestionIds = new ArrayList<>();
        selectQuestionsButton = findViewById(R.id.selectQuestionsButton);
        examLengthTextField = findViewById(R.id.examTimeTextField);
        pointsTextField = findViewById(R.id.pointsTextField);
        difficultyLabel = findViewById(R.id.difficultyLabel);
        difficultySlider = findViewById(R.id.difficultySlider);
        SharedPreferences sharedPreferences = getSharedPreferences("tr.yildiz.edu.tr.ahmetbarisyerlikaya.exam", MODE_PRIVATE);
        int examLength = sharedPreferences.getInt("examLength", DEFAULT_EXAM_LENGTH);
        int pointsPerQuestion = sharedPreferences.getInt("pointsPerQuestion", DEFAULT_POINTS_PER_QUESTION);
        int difficulty = sharedPreferences.getInt("difficulty", DEFAULT_DIFFICULTY);
        examLengthTextField.setText(Integer.toString(examLength));
        pointsTextField.setText(Integer.toString(pointsPerQuestion));
        difficultyLabel.setText("Difficulty: " + difficulty);
        difficultySlider.setProgress(difficulty);
        difficultySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                difficultyLabel.setText("Difficulty: " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }

    public void goToSelectQuestions(View view) {
        Intent intent = new Intent(CreateExamActivity.this, SelectQuestionsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedQuestionIds", selectedQuestionIds);
        intent.putExtras(bundle);
        intent.putExtra("userId", userId);
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_CODE) {
            Toast.makeText(CreateExamActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            return;
        }

        Bundle bundle = data.getExtras();
        selectedQuestionIds = (ArrayList<Integer>) bundle.getSerializable("selectedQuestionIds");
        selectQuestionsButton.setText("SELECT QUESTIONS (" + selectedQuestionIds.size() + ")");
    }

    private ArrayList<Question> getAllQuestions() {
        ArrayList<Question> questions = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM questions WHERE user_id = " + userId, null);
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String attachmentPath = cursor.getString(cursor.getColumnIndex("attachment_path"));
                String infoText = cursor.getString(cursor.getColumnIndex("info_text"));
                String questionText = cursor.getString(cursor.getColumnIndex("question_text"));
                String optionA = cursor.getString(cursor.getColumnIndex("option_a"));
                String optionB = cursor.getString(cursor.getColumnIndex("option_b"));
                String optionC = cursor.getString(cursor.getColumnIndex("option_c"));
                String optionD = cursor.getString(cursor.getColumnIndex("option_d"));
                String optionE = cursor.getString(cursor.getColumnIndex("option_e"));
                int correctAnswer = cursor.getInt(cursor.getColumnIndex("correct_answer"));
                if (attachmentPath.equals("null")) attachmentPath = null;
                Question question = new Question(id, attachmentPath, infoText, questionText, optionA, optionB, optionC, optionD, optionE, correctAnswer);
                questions.add(question);
            }
        } catch (Exception e) {
            Toast.makeText(CreateExamActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return questions;
    }

    private String generateTextFromExamData(ArrayList<Question> selectedQuestions) {
        String examLength = examLengthTextField.getText().toString();
        String pointsPerQuestion = pointsTextField.getText().toString();
        int difficulty = difficultySlider.getProgress();

        String examInformation = "Exam length: " + examLength + " minutes\n" +
                "Points per question: " + pointsPerQuestion + "\n" +
                "Difficulty: " + difficulty + " / 5";
        String content = "";

        int i = 1;
        for (Question question : selectedQuestions) {
            ArrayList<String> options = question.getOptionsByDifficulty(difficulty);
            String questionNumber = (i++) + ". ";
            String attachmentLine = question.getAttachmentPath() != null ? "[ATTACHMENT]\n" : "";
            String questionInfoLine = question.getInfoText() != null ? question.getInfoText() + "\n" : "";
            String questionTextLine = question.getQuestionText();
            String aLine = "\nA. " + options.get(0);
            String bLine = "\nB. " + options.get(1);
            String cLine = difficulty >= 3 ? "\nC. " + options.get(2) : "";
            String dLine = difficulty >= 4 ? "\nD. " + options.get(3) : "";
            String eLine = difficulty >= 5 ? "\nE. " + options.get(4) : "";
            content += "\n\n" + questionNumber + attachmentLine + questionInfoLine + questionTextLine + aLine + bLine + cLine + dLine + eLine;
        }

        String errorMessage = getErrorMessage(examLength, pointsPerQuestion, selectedQuestions);
        if (errorMessage != null) {
            Toast.makeText(CreateExamActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            return null;
        }

        return examInformation + content;
    }

    private String writeToExternalStorage(String text) {
        String path = getExternalStorageDirectory().getAbsolutePath() + "/documents";
        String fileName = "EXAM_" + generateTimestamp() + ".txt";
        File dir = new File(path);
        dir.mkdirs();
        File file = new File(dir, fileName);

        try {
            FileOutputStream out = new FileOutputStream(file);
            PrintWriter pw = new PrintWriter(out);
            pw.println(text);
            pw.flush();
            pw.close();
            out.close();
            return file.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }

    private String getErrorMessage(String examLength, String pointsPerQuestion, ArrayList<Question> selectedQuestions) {
        if (examLength == null || examLength.isEmpty() || parseInt(examLength) <= 0 || parseInt(examLength) >= MAX_VALUE)
            return "Exam length is not valid!";
        if (pointsPerQuestion == null || pointsPerQuestion.isEmpty() || parseInt(pointsPerQuestion) <= 0 || parseInt(pointsPerQuestion) >= MAX_VALUE)
            return "Points per question is not valid!";
        if (selectedQuestions == null || selectedQuestions.size() <= 0)
            return "At least one question must be selected!";
        return null;
    }

    public void saveExam(View view) {
        ArrayList<Question> questions = getAllQuestions();
        ArrayList<Question> selectedQuestions = new ArrayList<>();
        for (Question question : questions)
            if (selectedQuestionIds.contains(Integer.valueOf(question.getId())))
                selectedQuestions.add(question);
        String text = generateTextFromExamData(selectedQuestions);
        if (text == null) return;
        String filePath = writeToExternalStorage(text);
        if (filePath == null) {
            Toast.makeText(CreateExamActivity.this, "An error occurred!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(CreateExamActivity.this);
        builder.setTitle("Successful");
        builder.setMessage("File has been written into '" + filePath + "' successfully!");
        builder.setPositiveButton("SHARE FILE", (_d, _w) -> {
            Uri uri = Uri.parse(filePath);
            Intent intent = new Intent(ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(EXTRA_STREAM, uri);
            startActivity(Intent.createChooser(intent, "Sharing file"));
            finish();
        });
        builder.setNegativeButton("Cancel", (_d, _w) -> finish());
        builder.show();
    }
}