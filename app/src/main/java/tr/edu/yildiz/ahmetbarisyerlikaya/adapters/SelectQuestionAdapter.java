package tr.edu.yildiz.ahmetbarisyerlikaya.adapters;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import tr.edu.yildiz.ahmetbarisyerlikaya.R;
import tr.edu.yildiz.ahmetbarisyerlikaya.models.Question;

import static android.graphics.Typeface.BOLD;
import static android.view.View.VISIBLE;
import static tr.edu.yildiz.ahmetbarisyerlikaya.Utils.COLOR_GREEN;

public class SelectQuestionAdapter extends RecyclerView.Adapter<SelectQuestionAdapter.SelectQuestionViewHolder> {
    private final ArrayList<Question> questions;
    private final Context context;
    private final ArrayList<Integer> selectedQuestionIds;

    public SelectQuestionAdapter(ArrayList<Question> questions, ArrayList<Integer> selectedQuestionIds, Context context) {
        this.questions = questions;
        this.context = context;
        this.selectedQuestionIds = selectedQuestionIds;
    }

    @NotNull
    @Override
    public SelectQuestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.select_question_single_item, parent, false);
        return new SelectQuestionAdapter.SelectQuestionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectQuestionAdapter.SelectQuestionViewHolder holder, int position) {
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

        holder.checkBox.setChecked(selectedQuestionIds.contains(Integer.valueOf(id)));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) selectedQuestionIds.add(Integer.valueOf(id));
            else selectedQuestionIds.remove(Integer.valueOf(id));
        });

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
    }

    @Override
    public int getItemCount() { return questions.size(); }

    public class SelectQuestionViewHolder extends RecyclerView.ViewHolder {
        private CheckBox checkBox;
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


        public SelectQuestionViewHolder(@NonNull View itemView) {
            super(itemView);
            initialize();
        }

        private void initialize() {
            checkBox = itemView.findViewById(R.id.selectQuestionCheckBox);
            imageView = itemView.findViewById(R.id.questionImage2);
            videoView = itemView.findViewById(R.id.questionVideo2);
            playButton = itemView.findViewById(R.id.playAudioButton2);
            pauseButton = itemView.findViewById(R.id.pauseAudioButton2);
            stopButton = itemView.findViewById(R.id.stopAudioButton2);
            infoText = itemView.findViewById(R.id.questionInfoText2);
            questionText = itemView.findViewById(R.id.questionText2);
            optionsGroup = itemView.findViewById(R.id.questionRadioGroup2);
            optionA = itemView.findViewById(R.id.selectQuestionRadioButton1);
            optionB = itemView.findViewById(R.id.selectQuestionRadioButton2);
            optionC = itemView.findViewById(R.id.selectQuestionRadioButton3);
            optionD = itemView.findViewById(R.id.selectQuestionRadioButton4);
            optionE = itemView.findViewById(R.id.selectQuestionRadioButton5);
        }
    }
}
