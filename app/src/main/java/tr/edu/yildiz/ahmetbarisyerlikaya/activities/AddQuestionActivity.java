package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;

import static android.content.Intent.ACTION_OPEN_DOCUMENT;
import static android.content.Intent.CATEGORY_OPENABLE;
import static android.content.Intent.EXTRA_MIME_TYPES;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.generateTimestamp;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.getMimeTypeFromUri;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.saveToInternalStorage;

public class AddQuestionActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 2000;
    private ImageView imageView;
    private VideoView videoView;
    private FloatingActionButton playButton;
    private FloatingActionButton pauseButton;
    private FloatingActionButton stopButton;
    private TextView infoTextField;
    private TextView questionTextField;
    private TextView optionATextField;
    private TextView optionBTextField;
    private TextView optionCTextField;
    private TextView optionDTextField;
    private TextView optionETextField;
    private RadioButton optionARadio;
    private RadioButton optionBRadio;
    private RadioButton optionCRadio;
    private RadioButton optionDRadio;
    private RadioButton optionERadio;
    private Button submitButton;
    private Button attachMediaButton;
    private Uri fileUri;
    private String fileName;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_question);
        initialize();
    }

    private void initialize() {
        userId = getIntent().getStringExtra("userId");
        imageView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        playButton = findViewById(R.id.playButton);
        pauseButton = findViewById(R.id.pauseButton);
        stopButton = findViewById(R.id.stopButton);
        infoTextField = findViewById(R.id.questionInfoField);
        questionTextField = findViewById(R.id.questionTextField);
        optionATextField = findViewById(R.id.optionATextField);
        optionBTextField = findViewById(R.id.optionBTextField);
        optionCTextField = findViewById(R.id.optionCTextField);
        optionDTextField = findViewById(R.id.optionDTextField);
        optionETextField = findViewById(R.id.optionETextField);
        optionARadio = findViewById(R.id.optionARadio);
        optionBRadio = findViewById(R.id.optionBRadio);
        optionCRadio = findViewById(R.id.optionCRadio);
        optionDRadio = findViewById(R.id.optionDRadio);
        optionERadio = findViewById(R.id.optionERadio);
        submitButton = findViewById(R.id.addQuestionSubmitButton);
        attachMediaButton = findViewById(R.id.attachMediaButton);
        submitButton.setOnClickListener(__ -> createQuestion());
        attachMediaButton.setOnClickListener(__ -> attachMedia());
    }

    public void createQuestion() {
        String infoText = infoTextField.getText().toString();
        String questionText = questionTextField.getText().toString();
        String optionA = optionATextField.getText().toString();
        String optionB = optionBTextField.getText().toString();
        String optionC = optionCTextField.getText().toString();
        String optionD = optionDTextField.getText().toString();
        String optionE = optionETextField.getText().toString();
        int correctAnswer = getCorrectAnswer();

        String errorMessage = getErrorMessage(infoText, questionText, optionA, optionB, optionC, optionD, optionE, correctAnswer);
        if (errorMessage != null && !errorMessage.isEmpty()) {
            Toast.makeText(getApplicationContext(), errorMessage, LENGTH_SHORT).show();
            return;
        }

        String filePath = saveToInternalStorage(fileUri, fileName, getApplicationContext());
        SQLiteDatabase db = null;
        String message = null;
        boolean success = true;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("INSERT INTO questions" +
                    "(info_text, question_text, option_a, option_b, option_c, option_d, option_e, correct_answer, attachment_path, user_id) " +
                    "VALUES (" +
                    "'" + infoText + "'," +
                    "'" + questionText + "'," +
                    "'" + optionA + "'," +
                    "'" + optionB + "'," +
                    "'" + optionC + "'," +
                    "'" + optionD + "'," +
                    "'" + optionE + "'," +
                    correctAnswer + "," +
                    "'" + filePath + "'," +
                    userId + ")");
            message = "Question added successfully!";
        } catch (Exception e) {
            message = "An error occurred!";
            success = false;
        } finally {
            if (db != null) db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            if (success) finish();
        }
    }

    private int getCorrectAnswer() {
        if (optionARadio.isChecked()) return 0;
        if (optionBRadio.isChecked()) return 1;
        if (optionCRadio.isChecked()) return 2;
        if (optionDRadio.isChecked()) return 3;
        if (optionERadio.isChecked()) return 4;
        return -1; // No option selected
    }

    private String getErrorMessage(String infoText, String questionText, String optionA, String
            optionB, String optionC, String optionD, String optionE, int correctAnswer) {
        if (infoText == null || infoText.isEmpty()) return "Information text is not entered!";
        if (questionText == null || questionText.isEmpty()) return "Question text is not entered!";
        if (optionA == null || optionA.isEmpty()) return "Option A is not entered!";
        if (optionB == null || optionB.isEmpty()) return "Option B is not entered!";
        if (optionC == null || optionC.isEmpty()) return "Option C is not entered!";
        if (optionD == null || optionD.isEmpty()) return "Option D is not entered!";
        if (optionE == null || optionE.isEmpty()) return "Option E is not entered!";
        if (correctAnswer == -1) return "No option selected as correct answer!";
        return null;
    }

    public void attachMedia() {
        Intent intent = new Intent(ACTION_OPEN_DOCUMENT);
        intent.addCategory(CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.putExtra(EXTRA_MIME_TYPES, new String[]{"image/*", "video/*", "audio/*"});
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK || requestCode != REQUEST_CODE || data == null) return;

        Uri uri = data.getData();
        if (uri == null) {
            Toast.makeText(AddQuestionActivity.this, "An error occurred!", LENGTH_SHORT).show();
            return;
        }

        String fileName = generateTimestamp() + ".attachment";
        String mimeType = getMimeTypeFromUri(uri, getApplicationContext());

        if (mimeType.contains("image")) {
            Bitmap imageBitmap = null;
            try {
                if (Build.VERSION.SDK_INT >= 28)
                    imageBitmap = ImageDecoder.decodeBitmap(ImageDecoder.createSource(getContentResolver(), uri));
                else
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "An error occurred!", Toast.LENGTH_LONG).show();
            }
            fileName = "IMG_" + fileName;
            imageView.setImageBitmap(imageBitmap);
            imageView.setVisibility(VISIBLE);
        } else if (mimeType.contains("video")) {
            fileName = "VID_" + fileName;
            MediaController mediaController = new MediaController(AddQuestionActivity.this);
            mediaController.setAnchorView(videoView);
            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);
            videoView.setVisibility(VISIBLE);
        } else if (mimeType.contains("audio")) {
            fileName = "AUD_" + fileName;
            MediaPlayer mediaPlayer = MediaPlayer.create(AddQuestionActivity.this, uri);
            playButton.setOnClickListener(__ -> {
                mediaPlayer.start();
                pauseButton.setEnabled(true);
                stopButton.setEnabled(true);
            });
            pauseButton.setOnClickListener(__ -> {
                mediaPlayer.pause();
            });
            stopButton.setOnClickListener(__ -> {
                mediaPlayer.pause();
                mediaPlayer.seekTo(0);
            });
            playButton.setVisibility(VISIBLE);
            pauseButton.setVisibility(VISIBLE);
            stopButton.setVisibility(VISIBLE);
        } else {
            Toast.makeText(AddQuestionActivity.this, "File type not supported!", LENGTH_SHORT).show();
            return;
        }

        fileUri = uri;
        this.fileName = fileName;
    }
}