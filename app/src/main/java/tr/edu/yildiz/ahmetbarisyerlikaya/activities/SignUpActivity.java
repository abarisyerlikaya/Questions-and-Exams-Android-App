package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.Utils;

import static android.R.style.Theme_Holo_Light_Dialog_MinWidth;
import static android.widget.Toast.LENGTH_SHORT;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.convertBitmapToBytes;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.isDateStringValid;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.isEmailValid;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.isPhoneNumberValid;

public class SignUpActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1000;
    private ImageView userImageView;
    private EditText firstNameField;
    private EditText lastNameField;
    private EditText emailField;
    private EditText phoneNumberField;
    private EditText birthDateField;
    private EditText passwordField;
    private EditText reEnterPasswordField;
    private AlertDialog alertDialog;
    private OnDateSetListener dateSetListener;
    private Uri imageUri;
    private Bitmap imageBitmap;
    private String pickedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        initialize();
    }

    private void initialize() {
        userImageView = findViewById(R.id.userImageViewSignUp);
        firstNameField = findViewById(R.id.firstNameField);
        lastNameField = findViewById(R.id.lastNameField);
        emailField = findViewById(R.id.emailField2);
        phoneNumberField = findViewById(R.id.phoneNumberField);
        birthDateField = findViewById(R.id.birthDateField);
        passwordField = findViewById(R.id.passwordField2);
        reEnterPasswordField = findViewById(R.id.reEnterPasswordField);
        alertDialog = new AlertDialog.Builder(SignUpActivity.this).create();
        birthDateField.setOnFocusChangeListener((_v, hasFocus) -> { if (hasFocus) pickDate(); });
        dateSetListener = (_v, y, m, d) -> {
            pickedDate = "" + y + "-" + (m + 1) + "-" + d;
            birthDateField.setText(pickedDate);
        };
    }


    public void signUp(View view) {
        String firstName = firstNameField.getText().toString();
        String lastName = lastNameField.getText().toString();
        String email = emailField.getText().toString();
        String phoneNumber = phoneNumberField.getText().toString();
        String birthDate = birthDateField.getText().toString();
        String password = passwordField.getText().toString();
        String reEnterPassword = reEnterPasswordField.getText().toString();
        String errorMessage = getErrorMessage(firstName, lastName, email, phoneNumber, birthDate, password, reEnterPassword);

        byte[] imageBytes = null;
        if (imageBitmap != null) imageBytes = convertBitmapToBytes(imageBitmap);

        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(SignUpActivity.this, errorMessage, LENGTH_SHORT).show();
            return;
        }
        SQLiteDatabase db = null;
        String message = null;
        Intent intent = null;
        try {
            String encryptedPassword = Utils.encryptPassword(password);
            if (encryptedPassword == null) throw new Exception();

            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("INSERT INTO users " +
                    "(first_name, last_name, email, phone_number, birth_date, password_hashed" +
                    (imageBytes != null ? (", avatar") : "") + ") " +
                    "VALUES (" +
                    "'" + firstName + "'," +
                    "'" + lastName + "'," +
                    "'" + email + "'," +
                    "'" + phoneNumber + "'," +
                    "'" + birthDate + "'," +
                    "'" + encryptedPassword + "'" +
                    (imageBytes != null ? (", '" + imageBytes) + "'" : "") + ")");
            message = "Successfully signed up! Please sign in.";
            intent = new Intent(SignUpActivity.this, SignInActivity.class);
        } catch (Exception e) {
            message = "An error occurred while inserting into database.";
        } finally {
            db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            if (intent != null) {
                finish();
                startActivity(intent);
            }
        }
    }

    public void pickPhoto(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Pick Photo"), REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_CODE || resultCode != RESULT_OK) return;

        imageUri = data.getData();
        try {
            if (Build.VERSION.SDK_INT >= 28)
                imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), imageUri));
            else
                imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            userImageView.setImageBitmap(imageBitmap);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_LONG).show();
        }

    }

    private String getErrorMessage(String firstName, String lastName, String email, String gsm,
                                   String birthDate, String password, String reEnterPassword) {
        if (firstName == null || firstName.isEmpty())
            return "First name not entered!";
        if (lastName == null || lastName.isEmpty())
            return "Last name not entered!";
        if (email == null || email.isEmpty())
            return "E-mail not entered!";
        if (!isEmailValid(email))
            return "E-mail is not valid!";
        if (gsm == null || gsm.isEmpty())
            return "Phone number not entered!";
        if (!isPhoneNumberValid(gsm))
            return "Phone number is not valid!";
        if (birthDate == null || birthDate.isEmpty())
            return "Birth date not entered!";
        if (!isDateStringValid(birthDate))
            return "Birth date is not valid!";
        if (password == null || password.isEmpty())
            return "Password not entered!";
        if (password.length() < 6)
            return "Password must contain at least 6 characters!";
        if (reEnterPassword == null || reEnterPassword.isEmpty())
            return "Re-enter password not entered!";
        if (!password.equals(reEnterPassword))
            return "Passwords do not match!";
        if (doesNameExists(firstName, lastName))
            return "A user with this name already exists!";
        if (doesEmailExists(email))
            return "A user with this email already exists!";
        if (doesPhoneNumberExists(gsm))
            return "A user with this phone number already exists!";
        return null;
    }

    public void pickDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(YEAR);
        int month = calendar.get(MONTH);
        int day = calendar.get(DAY_OF_MONTH);

        DatePickerDialog pickerDialog = new DatePickerDialog(SignUpActivity.this,
                Theme_Holo_Light_Dialog_MinWidth, dateSetListener, year, month, day);
        pickerDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        pickerDialog.show();
    }

    private boolean doesNameExists(String firstName, String lastName) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean result = false;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM users " +
                    "WHERE first_name = '" + firstName + "' " +
                    "AND last_name = '" + lastName + "'", null);
            if (cursor.getCount() > 0) result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    private boolean doesEmailExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean result = false;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM users WHERE email = '" + email + "'", null);
            if (cursor.getCount() > 0) result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }

    private boolean doesPhoneNumberExists(String phoneNumber) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean result = false;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM users WHERE phone_number = '" + phoneNumber + "'", null);
            if (cursor.getCount() > 0) result = true;
        } catch (Exception e) {
            result = false;
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return result;
    }
}