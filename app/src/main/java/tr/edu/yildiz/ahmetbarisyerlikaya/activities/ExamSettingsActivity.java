package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.parseInt;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.DEFAULT_DIFFICULTY;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.DEFAULT_EXAM_LENGTH;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.DEFAULT_POINTS_PER_QUESTION;

public class ExamSettingsActivity extends AppCompatActivity {
    private EditText examLengthTextField;
    private EditText pointsTextField;
    private TextView difficultyLabel;
    private SeekBar difficultySlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_settings);
        initialize();
    }

    private void initialize() {
        examLengthTextField = findViewById(R.id.examTimeTextField2);
        pointsTextField = findViewById(R.id.pointsTextField2);
        difficultyLabel = findViewById(R.id.difficultyLabel2);
        difficultySlider = findViewById(R.id.difficultySlider2);
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

    private String getErrorMessage(String examLength, String pointsPerQuestion) {
        if (examLength == null || examLength.isEmpty() || parseInt(examLength) <= 0 || parseInt(examLength) >= MAX_VALUE)
            return "Exam length is not valid!";
        if (pointsPerQuestion == null || pointsPerQuestion.isEmpty() || parseInt(pointsPerQuestion) <= 0 || parseInt(pointsPerQuestion) >= MAX_VALUE)
            return "Points per question is not valid!";
        return null;
    }

    public void saveSettings(View view) {
        String examLength = examLengthTextField.getText().toString();
        String pointsPerQuestion = pointsTextField.getText().toString();
        int difficulty = difficultySlider.getProgress();

        String errorMessage = getErrorMessage(examLength, pointsPerQuestion);
        if (errorMessage != null) {
            Toast.makeText(ExamSettingsActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences sharedPreferences = getSharedPreferences("tr.yildiz.edu.tr.ahmetbarisyerlikaya.exam", MODE_PRIVATE);
        sharedPreferences.edit()
                .putInt("examLength", parseInt(examLength))
                .putInt("pointsPerQuestion", parseInt(pointsPerQuestion))
                .putInt("difficulty", difficulty)
                .apply();
        Toast.makeText(getApplicationContext(), "Global settings saved successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }
}