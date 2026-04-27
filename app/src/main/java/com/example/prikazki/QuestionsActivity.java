package com.example.prikazki;

import android.support.v7.app.AppCompatActivity;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;
import com.example.prikazki.models.Question;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {
    private QiContext qiContext;
    private String taleId; // don't ask why it's String
    private Question[] questions;
    private TextView questionText;
    private Button btn1,btn2,btn3,btnNext;
    private MediaPlayer mediaPlayer;
    private int questionId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);

        taleId = getIntent().getStringExtra("TALE_ID");

        questionText = (TextView) findViewById(R.id.questionText);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btnNext = (Button) findViewById(R.id.btn_skip);
        qiContext = (QiContext) this.getBaseContext();

        btnNext.setOnClickListener(v -> {
            questionId++;
            loadQuestions();
        });

        try {
            questions = Question.GetQuestionsFromTaleId(this,taleId);
        } catch (Exception e){
            Log.e("ERROR","Couldn't fetch question from questions JSON: "+e.getMessage());
        }

        setupQuizButtons();
    }

    private void setupQuizButtons() {
        View.OnClickListener quizListener = v -> {
            boolean isCorrect = false;
            int id = v.getId();

            btn3.getId();
            // Correct Answer Logic
            if (questionId == 0 && id == R.id.btn3) isCorrect = true; // Q1 -> 4
            else if (questionId == 1 && id == R.id.btn2) isCorrect = true; // Q2 -> Words
            else if (questionId == 2 && id == R.id.btn1) isCorrect = true; // Q3 -> Sun

            if (isCorrect) {
                Toast.makeText(this, "БРАВО!", Toast.LENGTH_SHORT).show();
                runAnimation(R.raw.final_nod);
                playFeedbackAudio("robot/gj.wav");
                btnNext.setText("Следващ въпрос");
                btnNext.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Опитай пак.", Toast.LENGTH_SHORT).show();
                runAnimation(R.raw.pain);
                playFeedbackAudio("robot/tryagain.wav");
            }
        };

        btn1.setOnClickListener(quizListener);
        btn2.setOnClickListener(quizListener);
        btn3.setOnClickListener(quizListener);
    }

    public void loadQuestions(){

    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            try {
                if (mediaPlayer.isPlaying()) mediaPlayer.stop();
                mediaPlayer.release();
            } catch (Exception e) {}
            mediaPlayer = null;
        }
    }

    private void playFeedbackAudio(String path) {
        try {
            releaseMediaPlayer();
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd(path);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {
            Log.e("ERROR", "Feedback audio error: " + e.getMessage());
        }
    }

    private void runAnimation(int resId){
        if (qiContext == null) return;
        AnimationBuilder.with(qiContext).withResources(resId).buildAsync().andThenConsume(animation ->
                AnimateBuilder.with(qiContext).withAnimation(animation).buildAsync().andThenConsume(animate -> animate.async().run())
        );
    }
}
