package tr.edu.yildiz.ahmetbarisyerlikaya.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.activities.EditQuestionActivity;
import tr.edu.yildiz.ahmetbarisyerlikaya.models.Question;

import static android.content.Context.MODE_PRIVATE;
import static android.graphics.Typeface.BOLD;
import static android.view.View.VISIBLE;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.COLOR_GREEN;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.QuestionViewHolder> {
    private final ArrayList<Question> questions;
    private final Context context;

    public QuestionAdapter(ArrayList<Question> questions, Context context) {
        this.questions = questions;
        this.context = context;
    }

    @NonNull
    @Override
    public QuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.question_single_item, parent, false);
        return new QuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionViewHolder holder, int position) {
        int id = questions.get(position).getId();
        String attachmentPath = questions.get(position).getAttachmentPath();
        String infoText = questions.get(position).getInfoText();
        String questionText = questions.get(position).getQuestionText();
        String optionA = questions.get(position).getOptionA();
        String optionB = questions.get(position).getOptionB();
        String optionC = questions.get(position).getOptionC();
        String optionD = questions.get(position).getOptionD();
        String optionE = questions.get(position).getOptionE();
        int correctAnswer = questions.get(position).getCorrectAnswer();

        if (attachmentPath != null) {
            String fileName = attachmentPath.substring(attachmentPath.lastIndexOf("/") + 1);
            if (fileName.startsWith("IMG_")) {
                holder.imageView.setImageBitmap(BitmapFactory.decodeFile(attachmentPath));
                holder.imageView.setVisibility(VISIBLE);
                holder.imageView.setMaxHeight(256);
            } else if (fileName.startsWith("VID_")) {
                MediaController mediaController = new MediaController(context);
                mediaController.setAnchorView(holder.videoView);
                mediaController.setMediaPlayer(holder.videoView);
                holder.videoView.setMediaController(mediaController);
                holder.videoView.setVideoURI(Uri.parse(attachmentPath));
                holder.videoView.setVisibility(VISIBLE);
            } else if (fileName.startsWith("AUD_")) {
                MediaPlayer mediaPlayer = MediaPlayer.create(context, Uri.parse(attachmentPath));
                holder.playButton.setOnClickListener(__ -> {
                    mediaPlayer.start();
                    holder.pauseButton.setEnabled(true);
                    holder.stopButton.setEnabled(true);
                });
                holder.pauseButton.setOnClickListener(__ -> {
                    mediaPlayer.pause();
                });
                holder.stopButton.setOnClickListener(__ -> {
                    mediaPlayer.pause();
                    mediaPlayer.seekTo(0);
                });
                holder.playButton.setVisibility(VISIBLE);
                holder.pauseButton.setVisibility(VISIBLE);
                holder.stopButton.setVisibility(VISIBLE);
            }
        }

        if (correctAnswer == 0) {
            holder.optionA.setTypeface(null, BOLD);
            holder.optionA.setTextColor(Color.parseColor(COLOR_GREEN));
        } else if (correctAnswer == 1) {
            holder.optionB.setTypeface(null, BOLD);
            holder.optionB.setTextColor(Color.parseColor(COLOR_GREEN));
        } else if (correctAnswer == 2) {
            holder.optionC.setTypeface(null, BOLD);
            holder.optionC.setTextColor(Color.parseColor(COLOR_GREEN));
        } else if (correctAnswer == 3) {
            holder.optionD.setTypeface(null, BOLD);
            holder.optionD.setTextColor(Color.parseColor(COLOR_GREEN));
        } else if (correctAnswer == 4) {
            holder.optionE.setTypeface(null, BOLD);
            holder.optionE.setTextColor(Color.parseColor(COLOR_GREEN));
        }

        holder.editButton.setOnClickListener(__ -> {
            Intent intent = new Intent(context, EditQuestionActivity.class);
            intent.putExtra("id", id);
            context.startActivity(intent);
        });
        holder.deleteButton.setOnClickListener(__ -> {
            new AlertDialog.Builder(context)
                    .setTitle("Confirmation")
                    .setMessage("Are you sure you want to delete the question permanently?")
                    .setPositiveButton("Yes", (_d, _w) -> {
                        deleteQuestion(questions.get(position));
                        notifyItemRemoved(position);
                    })
                    .setNegativeButton("No", null)
                    .show();
        });

        holder.infoText.setText(infoText);
        holder.questionText.setText(questionText);
        holder.optionA.setText(optionA);
        holder.optionB.setText(optionB);
        holder.optionC.setText(optionC);
        holder.optionD.setText(optionD);
        holder.optionE.setText(optionE);
        holder.optionA.setEnabled(false);
        holder.optionB.setEnabled(false);
        holder.optionC.setEnabled(false);
        holder.optionD.setEnabled(false);
        holder.optionE.setEnabled(false);
    }

    @Override
    public int getItemCount() {
        return questions.size();
    }

    private void deleteQuestion(Question question) {
        SQLiteDatabase db = null;
        try {
            db = context.openOrCreateDatabase("Database", MODE_PRIVATE, null);
            db.execSQL("DELETE FROM questions WHERE id = " + question.getId());
            questions.remove(question);
        } catch (Exception e) {
            Toast.makeText(context, "An error occurred!", Toast.LENGTH_SHORT).show();
        } finally {
            if (db != null) db.close();
        }
    }

    public class QuestionViewHolder extends RecyclerView.ViewHolder {
        private ImageView imageView;
        private VideoView videoView;
        private FloatingActionButton playButton;
        private FloatingActionButton pauseButton;
        private FloatingActionButton stopButton;
        private TextView infoText;
        private TextView questionText;
        private RadioGroup optionsGroup;
        private RadioButton optionA;
        private RadioButton optionB;
        private RadioButton optionC;
        private RadioButton optionD;
        private RadioButton optionE;
        private Button editButton;
        private Button deleteButton;

        public QuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            initialize();
        }

        private void initialize() {
            imageView = itemView.findViewById(R.id.questionImage);
            videoView = itemView.findViewById(R.id.questionVideo);
            playButton = itemView.findViewById(R.id.playAudioButton);
            pauseButton = itemView.findViewById(R.id.pauseAudioButton);
            stopButton = itemView.findViewById(R.id.stopAudioButton);
            infoText = itemView.findViewById(R.id.questionInfoText);
            questionText = itemView.findViewById(R.id.questionText);
            optionsGroup = itemView.findViewById(R.id.questionRadioGroup);
            optionA = itemView.findViewById(R.id.questionRadioButton1);
            optionB = itemView.findViewById(R.id.questionRadioButton2);
            optionC = itemView.findViewById(R.id.questionRadioButton3);
            optionD = itemView.findViewById(R.id.questionRadioButton4);
            optionE = itemView.findViewById(R.id.questionRadioButton5);
            editButton = itemView.findViewById(R.id.editQuestionButton);
            deleteButton = itemView.findViewById(R.id.deleteQuestionButton);
        }
    }
}
