package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.models.User;

import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.encryptPassword;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.isEmailValid;

public class SignInActivity extends AppCompatActivity {
    private ArrayList<User> users;
    private AlertDialog alertDialog;
    private EditText emailField;
    private EditText passwordField;
    private int attemptCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initialize();
    }

    @Override
    protected void onStart() {
        super.onStart();
        emailField.setText("");
        passwordField.setText("");
    }

    private void initialize() {
        alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        emailField = findViewById(R.id.emailField);
        passwordField = findViewById(R.id.passwordField);
        attemptCount = 0;
    }

    public void signIn(View view) {
        String email = emailField.getText().toString();
        String password = passwordField.getText().toString();
        String passwordHashed = !password.isEmpty() ? encryptPassword(password) : null;
        String errorMessage = getErrorMessage(email, passwordHashed);

        // There is a client error
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(getApplicationContext(), errorMessage, LENGTH_SHORT).show();
            handleAttempts();
            return;
        }

        String userId = getColumnFromDb(email, "id");
        if (userId == null) return;

        Intent intent = new Intent(SignInActivity.this, MenuActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    private String getErrorMessage(String email, String password) {
        if (email == null || email.isEmpty())
            return "E-mail not entered!";
        if (!isEmailValid(email))
            return "Invalid email format!";
        if (password == null || password.isEmpty())
            return "Password not entered!";

        String originalPassword = getColumnFromDb(email, "password_hashed");
        if (originalPassword == null || originalPassword.isEmpty() || !password.equals(originalPassword))
            return "Invalid email or password";
        return null;
    }

    private String getColumnFromDb(String email, String desiredColumn) {
        String result = null;
        Cursor cursor = null;
        SQLiteDatabase db = null;

        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT " + desiredColumn + " FROM users WHERE email = '" + email + "';", null);

            if (db == null || cursor == null) throw new Exception("An error occurred!");
            else if (cursor.getCount() > 0) {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndex(desiredColumn));
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error occurred!", LENGTH_SHORT).show();
        }

        if (cursor != null) cursor.close();
        if (db != null) db.close();

        return result;
    }

    private void handleAttempts() {
        if (++attemptCount < 3) return;

        AlertDialog alertDialog = new AlertDialog.Builder(SignInActivity.this).create();
        alertDialog.setTitle("Warning");
        alertDialog.setMessage("You have reached 3 failed sign in attempts. You probably don't have an account.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "CREATE AN ACCOUNT",
                (_d, _w) -> startActivity(new Intent(SignInActivity.this, SignUpActivity.class)));
        alertDialog.show();
    }
}