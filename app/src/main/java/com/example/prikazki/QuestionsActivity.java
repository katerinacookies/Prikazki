package com.example.prikazki;

import android.content.Intent;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionsActivity extends AppCompatActivity {
    private QiContext qiContext;
    private String taleId; // don't ask why it's String
    private Question[] questions;
    private TextView questionText;
    private Button btn1,btn2,btn3,btnNext;
    private MediaPlayer mediaPlayer;
    private int questionId = 0;
    private Map<Integer, Integer> questionBtnIds = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Log.e("T_ERROR","in questioons activity");
        //Toast.makeText(this, "ENTER QUESTIONSs", Toast.LENGTH_SHORT).show();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions);

        Log.e("T_ERROR","in questioons activity 2");

        taleId = getIntent().getStringExtra("TALE_ID");

        questionText = (TextView) findViewById(R.id.questionText);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);
        btnNext = (Button) findViewById(R.id.btn_skip);
        //qiContext = (QiContext) this.getBaseContext();

        Log.e("T_ERROR","in questioons activity 3");
        questionBtnIds.put(0,2131165206); questionBtnIds.put(1,2131165207); questionBtnIds.put(2,2131165208);
        Log.e("T_ERROR","in questioons activity 4");

        btnNext.setOnClickListener(v -> {
            questionId++;

            if(questionId==3) {
                Intent intent = new Intent(QuestionsActivity.this, TaleSelectionActivity.class);
                intent.putExtra("GROUP_ID", intent.getStringExtra("GROUP_ID"));
                startActivity(intent);
            }

            btnNext.setText("Пропусни");
            loadQuestions();
        });

        Log.e("T_ERROR","in questioons activity 5");
        try {
            questions = Question.GetQuestionsFromTaleId(this,taleId);
        } catch (Exception e){
            Log.e("ERROR","Couldn't fetch question from questions JSON: "+e.getMessage());
        }
        loadQuestions();
        Log.e("T_ERROR","in questioons activity 6");
        setupQuizButtons();
        Log.e("T_ERROR","in questioons activity 7");
    }

    private void setupQuizButtons() {
        View.OnClickListener quizListener = v -> {
            boolean isCorrect = false;
            int pressedId = v.getId();

            // Correct Answer Logic
            if (pressedId == questionBtnIds.get(questions[questionId].rightAnswerId)) isCorrect = true;

            if (isCorrect) {
                Toast.makeText(this, "БРАВО!", Toast.LENGTH_SHORT).show();
                //runAnimation(R.raw.point_air);
                playFeedbackAudio("robot/mp3/gj.mp3");
                btnNext.setText("Следващ въпрос");
                btnNext.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(this, "Опитай пак.", Toast.LENGTH_SHORT).show();
                //runAnimation(R.raw.think);
                playFeedbackAudio("robot/mp3/tryagain.mp3");
            }
        };

        btn1.setOnClickListener(quizListener);
        btn2.setOnClickListener(quizListener);
        btn3.setOnClickListener(quizListener);
    }

    public void loadQuestions(){
        Question currQuestion = questions[questionId];

        runOnUiThread(() -> {
            btn1.setText(questions[questionId].answers[0].text);
            btn2.setText(questions[questionId].answers[1].text);
            btn3.setText(questions[questionId].answers[2].text);
            questionText.setText(questions[questionId].text);
        });

        playQuestionAudio(currQuestion);
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

    private void playQuestionAudio(Question question) {
        playAudio(question.audioDir, () -> {
            playAudio(question.answers[0].audioDir, () -> {
                playAudio(question.answers[1].audioDir, () -> {
                    playAudio(question.answers[2].audioDir, () -> { });
                });
            });
        });
    }

    private void playAudio(String fileName, Runnable onComplete) {
        releaseMediaPlayer();

        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd("robot/questions/" + fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                if (onComplete != null) onComplete.run();
            });
            mediaPlayer.start();
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            //Log.e("ERROR", e.getMessage());
            if (onComplete != null) onComplete.run();
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
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
