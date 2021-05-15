package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;

public class MainActivity extends AppCompatActivity {
    View goToSignInButton;
    View goToSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialize();
        createDatabaseIfNotExist();
    }

    private void createDatabaseIfNotExist() {
        SQLiteDatabase db = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("CREATE TABLE IF NOT EXISTS users(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "first_name VARCHAR," +
                    "last_name VARCHAR," +
                    "email VARCHAR UNIQUE," +
                    "phone_number VARCHAR UNIQUE," +
                    "birth_date DATE," +
                    "password_hashed VARCHAR," +
                    "avatar BLOB)");
            db.execSQL("CREATE TABLE IF NOT EXISTS questions(" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "user_id INTEGER," +
                    "info_text VARCHAR," +
                    "question_text VARCHAR," +
                    "option_a VARCHAR," +
                    "option_b VARCHAR," +
                    "option_c VARCHAR," +
                    "option_d VARCHAR," +
                    "option_e VARCHAR," +
                    "correct_answer INTEGER," +
                    "attachment_path VARCHAR)");
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_SHORT).show();
        }
        if (db != null) db.close();
    }

    private void initialize() {
        goToSignInButton = findViewById(R.id.goToSignIn);
        goToSignUpButton = findViewById(R.id.goToSignUp);
    }

    public void startSignInActivity(View view) {
        startActivity(new Intent(MainActivity.this, SignInActivity.class));
    }

    public void startSignUpActivity(View view) {
        startActivity(new Intent(MainActivity.this, SignUpActivity.class));
    }
}