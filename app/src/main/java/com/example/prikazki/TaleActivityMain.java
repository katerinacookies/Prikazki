package com.example.prikazki;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import android.util.Log;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


//logikata za prikazkite za 1, 2 i 3ta grupa
public class TaleActivityMain extends AppCompatActivity implements RobotLifecycleCallbacks{
    private QiContext qiContext;
    private MediaPlayer mediaPlayer;
    private JSONObject taleData;
    private JSONArray stepsArray;
    private int currentStep = -1; // -1 = Intro, 0+ = Story steps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_player);

        String taleId = getIntent().getStringExtra("TALE_ID");
        loadTaleFromJSON(taleId);

        findViewById(R.id.btnBackToList).setOnClickListener(v -> finish());
        QiSDK.register(this, this);
    }

    @Override
    public void onRobotFocusGained(QiContext context) {
        this.qiContext = context;
        startTaleIntro();
    }

    private void startTaleIntro() {
        try {
            String title = taleData.getString("title");
            String author = taleData.getString("author");
            String titleAudio = taleData.getString("title_audio");

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.txtTitle)).setText(title);
                ((TextView) findViewById(R.id.txtAuthor)).setText(author);
            });

            // 1. Play Title Audio
            playAudio(titleAudio, () -> {
                // 2. Play Author Audio
                try {
                    playAudio(taleData.getString("author_audio"), () -> {
                        // 3. Start Steps
                        runOnUiThread(() -> {
                            findViewById(R.id.headerLayout).setVisibility(View.GONE);
                            findViewById(R.id.storyImageView).setVisibility(View.VISIBLE);
                            findViewById(R.id.btnQuestions).setVisibility(View.VISIBLE);
                        });
                        nextStep();
                    });
                } catch (Exception e) {
                    nextStep();
                }
            });
        } catch (Exception e) {
            finish();
        }
    }

    private void nextStep() {
        currentStep++;
        if (currentStep >= stepsArray.length()) return; // Story finished!

        try {
            JSONObject step = stepsArray.getJSONObject(currentStep);

            // Show Image
            String imgName = step.getString("image");
            int resID = getResources().getIdentifier(imgName, "drawable", getPackageName());
            runOnUiThread(() -> ((ImageView) findViewById(R.id.storyImageView)).setImageResource(resID));

            // Run Animation Chain (from JSON array)
            JSONArray anims = step.getJSONArray("animations");
            runAnimationChain(anims, 0);

            // Play Step Audio
            playAudio(step.getString("audio"), () -> {
                // When audio finishes, wait a beat and go to next step
                new Handler().postDelayed(this::nextStep, 1000);
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void playAudio(String fileName, Runnable onComplete) {
        releaseMediaPlayer();
        try {
            mediaPlayer = new MediaPlayer();
            AssetFileDescriptor afd = getAssets().openFd("audio/" + fileName);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            afd.close();
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(mp -> {
                if (onComplete != null) onComplete.run();
            });
            mediaPlayer.start();
        } catch (Exception e) {
            if (onComplete != null) onComplete.run();
        }
    }

    private void runAnimationChain(JSONArray anims, int index) {
        if (index >= anims.length() || qiContext == null) return;
        try {
            String animName = anims.getString(index);
            int resId = getResources().getIdentifier(animName, "raw", getPackageName());
            RobotHelper.runAnimation(qiContext, resId, () -> runAnimationChain(anims, index + 1));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onDestroy() {
        releaseMediaPlayer();
        QiSDK.unregister(this, this);
        super.onDestroy();
    }

    private void loadTaleFromJSON(String targetTaleId) {
        try {
            // 1. Read the JSON file from the assets folder
            InputStream is = getAssets().open("tales_config.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String jsonString = new String(buffer, StandardCharsets.UTF_8);
            JSONObject fullConfig = new JSONObject(jsonString);

            // 2. We need to check all groups (group1, group2, group3)
            // to find the tale with the matching ID
            String[] groups = {"group1", "group2", "group3"};
            boolean found = false;

            for (String groupKey : groups) {
                if (fullConfig.has(groupKey)) {
                    JSONArray groupArray = fullConfig.getJSONArray(groupKey);
                    for (int i = 0; i < groupArray.length(); i++) {
                        JSONObject tale = groupArray.getJSONObject(i);
                        if (tale.getString("id").equals(targetTaleId)) {
                            this.taleData = tale;
                            this.stepsArray = tale.getJSONArray("steps");
                            found = true;
                            break;
                        }
                    }
                }
                if (found) break;
            }

            if (!found) {
                Toast.makeText(this, "Приказката не е намерена!", Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            Log.e("JSON_ERROR", "Error loading tale: " + e.getMessage());
            finish();
        }
    }

    @Override
    public void onRobotFocusLost() {
        // This is called if someone touches Pepper's head or a safety triggers
        this.qiContext = null;
        releaseMediaPlayer();
    }

    @Override
    public void onRobotFocusRefused(String reason) {
        // This is called if the robot is busy or in an error state
        Log.e("QiSDK", "Focus refused: " + reason);
    }
}
