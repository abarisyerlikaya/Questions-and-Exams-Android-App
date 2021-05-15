package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.adapters.QuestionAdapter;
import tr.edu.yildiz.ahmetbarisyerlikaya.models.Question;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class QuestionsActivity extends AppCompatActivity {
    private TextView noQuestionsText;
    private RecyclerView questionRecycler;
    private ArrayList<Question> questions;
    private QuestionAdapter questionAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_question_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = new Intent(QuestionsActivity.this, AddQuestionActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }


    private void initialize() {
        userId = getIntent().getStringExtra("userId");
        noQuestionsText = findViewById(R.id.noQuestionsText);
        questionRecycler = findViewById(R.id.questionRecycler);
    }

    private void handleData() {
        questions = new ArrayList<>();
        fetchQuestionData();
        questionAdapter = new QuestionAdapter(questions, this);
        questionRecycler.setAdapter(questionAdapter);
        questionRecycler.setLayoutManager(new LinearLayoutManager(this));
        questionAdapter.notifyDataSetChanged();
        noQuestionsText.setVisibility(questions.size() == 0 ? VISIBLE : GONE);
    }

    public void fetchQuestionData() {
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
            Toast.makeText(QuestionsActivity.this, "An error occurred!", Toast.LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }
}