package com.example.prikazki;
//package com.example.prikazki.models;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.example.prikazki.models.Tale;


//logikata za prikazkite za 1, 2 i 3ta grupa
public class TaleActivityMain extends AppCompatActivity implements RobotLifecycleCallbacks{
    private QiContext qiContext;
    private MediaPlayer mediaPlayer;
    private Tale currentTale;
    private int currentStep = -1; // -1 = Intro, 0+ = Story steps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_player);

        try {
            String taleIdString = getIntent().getStringExtra("TALE_ID");
            String taleId = taleIdString;
            loadTaleFromJSON(taleId);
        } catch (Exception e){
            Log.e("JSON_ERROR", "Error loading tale: " + e.getMessage());
            //Toast.makeText(this, "JSON Loading Failed!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

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
            String title = currentTale.name;
            String authorName = currentTale.authorName;
            String authorAudio = currentTale.authorAudio;

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.txtTitle)).setText(title);
                ((TextView) findViewById(R.id.txtAuthor)).setText(authorName);
            });

            // 1. Play Title Audio
            playAudio(authorAudio, () -> {
                // 2. Play Author Audio
                try {
                    playAudio(authorAudio, () -> {
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
        if (currentStep >= currentTale.pics.length) return; // Story finished!

        try {
            // Show Image
            String imgName = currentTale.pics[currentStep];
            int resID = getResources().getIdentifier(imgName, "drawable", getPackageName());
            runOnUiThread(() -> ((ImageView) findViewById(R.id.storyImageView)).setImageResource(resID));

            // Run Animation Chain
            String[] animations = currentTale.animations[currentStep];
            runAnimationChain(animations, 0);

            // Play Step Audio
            String audio = "robot/"+currentTale.soundsPath+"_"+currentStep+".mp3";

            playAudio(audio, () -> {
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
            AssetFileDescriptor afd = getAssets().openFd(fileName);
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

    private void runAnimationChain(String[] animations, int index) {
        if (index >= animations.length || qiContext == null) return;
        try {
            String animName = animations[index];
            int resId = getResources().getIdentifier(animName, "raw", getPackageName());
            RobotHelper.runAnimation(qiContext, resId, () -> runAnimationChain(animations, index + 1));
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
            currentTale = Tale.GetTaleDataFromId(this, targetTaleId);

            if(currentTale == null) {
                Toast.makeText(this, "JSON Loading Failed! Tale id: " + targetTaleId, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Log.e("JSON_ERROR", "Error loading tale (loadTaleFromJSON): " + e.getMessage());
            //Toast.makeText(this, "$JSON Loading Failed!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
