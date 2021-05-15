package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;

import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.verifyStoragePermissions;

public class MenuActivity extends AppCompatActivity {
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        verifyStoragePermissions(MenuActivity.this);
        userId = getIntent().getStringExtra("userId");
    }

    public void goToExamSettings(View view) {
        startActivity(new Intent(MenuActivity.this, ExamSettingsActivity.class));
    }

    public void goToCreateExam(View view) {
        Intent intent = new Intent(MenuActivity.this, CreateExamActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void goToQuestions(View view) {
        Intent intent = new Intent(MenuActivity.this, QuestionsActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(MenuActivity.this)
                .setTitle("Signing out")
                .setMessage("Are you sure you want to sign out?")
                .setPositiveButton("Yes", (dialog, which) -> finish())
                .setNegativeButton("No", null)
                .show();
    }
}