package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.adapters.SelectQuestionAdapter;
import tr.edu.yildiz.ahmetbarisyerlikaya.models.Question;

public class SelectQuestionsActivity extends AppCompatActivity {
    private RecyclerView selectQuestionRecycler;
    private ArrayList<Question> questions;
    private ArrayList<Integer> selectedQuestionIds;
    private SelectQuestionAdapter selectQuestionAdapter;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_questions);
        initialize();
    }

    private void initialize() {
        userId = getIntent().getStringExtra("userId");
        selectQuestionRecycler = findViewById(R.id.selectQuestionRecycler);
        questions = new ArrayList<>();
        selectedQuestionIds = getIntent().getExtras().getIntegerArrayList("selectedQuestionIds");
        fetchQuestionData();
        selectQuestionAdapter = new SelectQuestionAdapter(questions, selectedQuestionIds, this);
        selectQuestionRecycler.setAdapter(selectQuestionAdapter);
        selectQuestionRecycler.setLayoutManager(new LinearLayoutManager(this));
        selectQuestionAdapter.notifyDataSetChanged();
    }

    private void fetchQuestionData() {
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
            Toast.makeText(SelectQuestionsActivity.this, "An error occurred!", Toast.LENGTH_SHORT);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
    }

    public void sendResult(View view) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putSerializable("selectedQuestionIds", selectedQuestionIds);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}