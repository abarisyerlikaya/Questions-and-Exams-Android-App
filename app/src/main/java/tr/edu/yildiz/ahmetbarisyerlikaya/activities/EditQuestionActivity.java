package tr.edu.yildiz.ahmetbarisyerlikaya.activities;

import android.content.Intent;
import android.database.Cursor;
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
import static android.graphics.BitmapFactory.decodeFile;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.widget.Toast.LENGTH_SHORT;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.generateTimestamp;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.getMimeTypeFromUri;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.saveToInternalStorage;

public class EditQuestionActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 3000;
    private int id;
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
    private boolean isAttachmentUpdated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_question);
        initialize();
    }

    private void initialize() {
        id = getIntent().getIntExtra("id", -1);
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
        submitButton.setOnClickListener(__ -> saveQuestion());
        attachMediaButton.setOnClickListener(__ -> attachMedia());
        fetchQuestionData();
    }

    private int getCorrectAnswer() {
        if (optionARadio.isChecked()) return 0;
        if (optionBRadio.isChecked()) return 1;
        if (optionCRadio.isChecked()) return 2;
        if (optionDRadio.isChecked()) return 3;
        if (optionERadio.isChecked()) return 4;
        return -1; // No option selected
    }

    private void saveQuestion() {
        String attachmentPath = null;
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

        if (isAttachmentUpdated) attachmentPath = saveToInternalStorage(fileUri, fileName, this);
        if (optionARadio.isChecked()) correctAnswer = 0;
        else if (optionBRadio.isChecked()) correctAnswer = 1;
        else if (optionCRadio.isChecked()) correctAnswer = 2;
        else if (optionDRadio.isChecked()) correctAnswer = 3;
        else if (optionERadio.isChecked()) correctAnswer = 4;

        SQLiteDatabase db = null;
        String message = null;
        boolean success = true;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("UPDATE questions SET " +
                    (isAttachmentUpdated ? ("attachment_path = '" + attachmentPath + "',") : "") +
                    "info_text = '" + infoTextField.getText().toString() + "'," +
                    "question_text = '" + questionTextField.getText().toString() + "'," +
                    "option_a = '" + optionATextField.getText().toString() + "'," +
                    "option_b = '" + optionBTextField.getText().toString() + "'," +
                    "option_c = '" + optionCTextField.getText().toString() + "'," +
                    "option_d = '" + optionDTextField.getText().toString() + "'," +
                    "option_e = '" + optionETextField.getText().toString() + "'," +
                    "correct_answer = " + correctAnswer +
                    " WHERE id = " + id);
            message = "Question updated successfully!";
        } catch (Exception e) {
            message = "An error occurred!";
            success = false;
        } finally {
            if (db != null) db.close();
            Toast.makeText(getApplicationContext(), message, LENGTH_SHORT).show();
            if (success) finish();
        }
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

    private void fetchQuestionData() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = openOrCreateDatabase("Database", MODE_PRIVATE, null);
            cursor = db.rawQuery("SELECT * FROM questions WHERE id = " + id, null);
            System.out.println("Query successful!");

            cursor.moveToFirst();
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
            else {
                fileUri = Uri.parse(attachmentPath);
                fileName = attachmentPath.substring(attachmentPath.lastIndexOf("/") + 1);

                if (fileName.startsWith("IMG_")) {
                    imageView.setImageBitmap(decodeFile(fileUri.getPath()));
                    imageView.setVisibility(VISIBLE);
                } else if (fileName.startsWith("VID_")) {
                    MediaController mediaController = new MediaController(EditQuestionActivity.this);
                    mediaController.setAnchorView(videoView);
                    mediaController.setMediaPlayer(videoView);
                    videoView.setMediaController(mediaController);
                    videoView.setVideoURI(fileUri);
                    videoView.setVisibility(VISIBLE);
                } else if (fileName.startsWith("AUD_")) {
                    MediaPlayer mediaPlayer = MediaPlayer.create(EditQuestionActivity.this, fileUri);
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
                }
            }

            isAttachmentUpdated = false;
            infoTextField.setText(infoText);
            questionTextField.setText(questionText);
            optionATextField.setText(optionA);
            optionBTextField.setText(optionB);
            optionCTextField.setText(optionC);
            optionDTextField.setText(optionD);
            optionETextField.setText(optionE);

            if (correctAnswer == 0) optionARadio.setChecked(true);
            else if (correctAnswer == 1) optionBRadio.setChecked(true);
            else if (correctAnswer == 2) optionCRadio.setChecked(true);
            else if (correctAnswer == 3) optionDRadio.setChecked(true);
            else if (correctAnswer == 4) optionERadio.setChecked(true);
        } catch (Exception e) {
            System.out.println("This is the error fetch data!");
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
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
            Toast.makeText(EditQuestionActivity.this, "An error occurred!", LENGTH_SHORT).show();
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
            videoView.setVisibility(GONE);
            playButton.setVisibility(GONE);
            pauseButton.setVisibility(GONE);
            stopButton.setVisibility(GONE);
        } else if (mimeType.contains("video")) {
            fileName = "VID_" + fileName;
            MediaController mediaController = new MediaController(EditQuestionActivity.this);
            mediaController.setAnchorView(videoView);
            mediaController.setMediaPlayer(videoView);
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(uri);
            videoView.setVisibility(VISIBLE);
            imageView.setVisibility(GONE);
            playButton.setVisibility(GONE);
            pauseButton.setVisibility(GONE);
            stopButton.setVisibility(GONE);
        } else if (mimeType.contains("audio")) {
            fileName = "AUD_" + fileName;
            MediaPlayer mediaPlayer = MediaPlayer.create(EditQuestionActivity.this, uri);
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
            videoView.setVisibility(GONE);
            imageView.setVisibility(GONE);
        } else {
            Toast.makeText(EditQuestionActivity.this, "File type not supported!", LENGTH_SHORT).show();
            return;
        }

        this.fileUri = uri;
        this.fileName = fileName;
        this.isAttachmentUpdated = true;
    }
}