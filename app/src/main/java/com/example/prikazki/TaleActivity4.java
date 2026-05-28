package com.example.prikazki;

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
import com.example.prikazki.models.Tale4;


//logikata za prikazkite za 4ta grupa
public class TaleActivity4 extends AppCompatActivity implements RobotLifecycleCallbacks {
    //!ADDED THIS BOOLEAN FLAG FOR PHONE EMULATION
    public boolean isEmulatorMode = false;

    private QiContext qiContext;
    private MediaPlayer mediaPlayer;
    private Tale4 currentTale;
    private int currentStep = -1; // -1 = Intro, 0+ = Story steps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("DEBUG", "Hello world");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale_player);

        try {
            String taleId = getIntent().getStringExtra("TALE_ID");
            String talePartId = getIntent().getStringExtra("TALE_PART_ID");
            loadTaleFromJSON(taleId, talePartId);
        } catch (Exception e){
            Log.e("JSON_ERROR", "Error loading tale: " + e.getMessage());
            //Toast.makeText(this, "JSON Loading Failed!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.btnBackToList).setOnClickListener(v -> finish());

        if (isEmulatorMode) {
            // Bypass the robot and start the tale after a 1-second delay
            new Handler().postDelayed(this::startTaleIntro, 1000);
        }
        else{
            QiSDK.register(this, this);
        }
    }

    @Override
    public void onRobotFocusGained(QiContext context) {
        this.qiContext = context;
        startTaleIntro();
    }

    private void startTaleIntro() {
        try {
            String title = currentTale.name;
            String titleAudio = currentTale.titleAudio;

            runOnUiThread(() -> {
                ((TextView) findViewById(R.id.txtTitle)).setText(title);
                findViewById(R.id.btnQuestions).setVisibility(View.GONE);
            });

            // Get the current part ID safely
            String partId = getIntent().getStringExtra("TALE_PART_ID");

            try {
                if ("0".equals(partId)) {
                    // Part 0 plays the title audio, then switches over to nextStep() safely
                    playAudio(titleAudio, () -> {
                        runOnUiThread(() -> {
                            findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
                            findViewById(R.id.storyImageView).setVisibility(View.VISIBLE);
                            findViewById(R.id.btnQuestions).setVisibility(View.GONE);
                            ((TextView) findViewById(R.id.txtTitle)).setText("");
                        });
                        nextStep();
                    });
                } else {
                    // Parts 1 & 2 skip intro audio. We MUST execute nextStep() inside the UI Thread block!
                    runOnUiThread(() -> {
                        findViewById(R.id.headerLayout).setVisibility(View.VISIBLE);
                        findViewById(R.id.storyImageView).setVisibility(View.VISIBLE);
                        findViewById(R.id.btnQuestions).setVisibility(View.GONE);
                        ((TextView) findViewById(R.id.txtTitle)).setText("");

                        // Fixed: Forcing the story loops for Middle/End to start cleanly on the Main thread
                        nextStep();
                    });
                }

            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void nextStep() {
        //check for stuff
        if (android.os.Looper.myLooper() != android.os.Looper.getMainLooper()) {
            runOnUiThread(this::nextStep);
            return;
        }


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
            String talePartId = getIntent().getStringExtra("TALE_PART_ID");
            String audio = currentTale.soundsPath + "_" +talePartId+"_"+ currentStep;

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
            AssetFileDescriptor afd = getAssets().openFd("robot/mp3/" + fileName + ".mp3");
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

    private void runAnimationChain(String[] animations, int index) {
        // If we finished the array or context is lost, stop
        if (animations == null || index >= animations.length || qiContext == null) {
            return;
        }

        try {
            String animName = animations[index];
            animName = animName.replace(".qianim", "");

            int resId = getResources().getIdentifier(animName, "raw", getPackageName());

            // REMOVED: The Toast message that was showing labels on screen and crashing background threads

            if (isEmulatorMode) {
                runAnimationChain(animations, index + 1);
            } else {
                // Run the animation on Pepper
                RobotHelper.runAnimation(qiContext, resId, () -> {
                    // FIX: Force the next animation in the chain to execute safely
                    // back on the Main UI Thread to keep the chain alive
                    runOnUiThread(() -> runAnimationChain(animations, index + 1));
                });
            }
        } catch (Exception e) {
            Log.e("ANIMATION_ERROR", "Error playing animation index " + index + ": " + e.getMessage());
            // Fallback: Try to play the next animation anyway if one fails
            runOnUiThread(() -> runAnimationChain(animations, index + 1));
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

    private void loadTaleFromJSON(String targetTaleId, String talePartId) {
        try {
            currentTale = Tale4.GetTaleDataFromId(this, targetTaleId, talePartId);

            if  (currentTale == null) {
                throw new Exception("JSON Loading Failed! Tale id: " + targetTaleId);
            }

            //invokes an exception when something is wrong
            currentTale.IsValid();

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
