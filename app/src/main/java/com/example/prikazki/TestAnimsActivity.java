package com.example.prikazki;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import com.aldebaran.qi.Future;
import com.aldebaran.qi.sdk.QiContext;
import com.aldebaran.qi.sdk.QiSDK;
import com.aldebaran.qi.sdk.RobotLifecycleCallbacks;
import com.aldebaran.qi.sdk.builder.AnimateBuilder;
import com.aldebaran.qi.sdk.builder.AnimationBuilder;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class TestAnimsActivity extends AppCompatActivity implements RobotLifecycleCallbacks {

    private static final String TAG = "PepperAnimTest";
    private QiContext qiContext;
    private Future<Void> currentAnimFuture;
    private GridLayout buttonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_anims);

        buttonContainer = (GridLayout) findViewById(R.id.buttonContainer);

        // Safety: Back button logic
        if (findViewById(R.id.btnBackToList) != null) {
            findViewById(R.id.btnBackToList).setOnClickListener(v -> finish());
        }

        // 1. Fill the screen with buttons immediately
        try {
            generateButtons();
        } catch (Exception e) {
            Log.e(TAG, "Failed to generate buttons: " + e.getMessage());
        }

        // 2. Register with the robot
        QiSDK.register(this, this);
    }

    private void generateButtons() {
        List<Integer> animResources = getAllAnimationResources();
        if (animResources.isEmpty()) {
            Toast.makeText(this, "No .qianim files found in res/raw!", Toast.LENGTH_LONG).show();
            return;
        }

        for (Integer resId : animResources) {
            Button btn = new Button(this);
            String rawName = getResources().getResourceEntryName(resId);
            btn.setText(rawName.toUpperCase().replace("_", " "));

            // Set margins so they don't touch
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(10, 10, 10, 10);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> playAnimation(resId));
            buttonContainer.addView(btn);
        }
    }

    private void playAnimation(int resId) {
        Log.d("TEST", "Button pressed for ID: " + resId);
        Toast.makeText(this, "Button pressed for ID: " + resId, Toast.LENGTH_SHORT).show();

        // SAFETY 1: Check context
        if (qiContext == null) {
            Toast.makeText(this, "Robot not ready yet!", Toast.LENGTH_SHORT).show();
            return;
        }

        // SAFETY 2: Cancel previous animation
        if (currentAnimFuture != null) {
            currentAnimFuture.requestCancellation();
        }

        // SAFETY 3: Run robot logic in background (buildAsync)
        AnimationBuilder.with(qiContext)
                .withResources(resId)
                .buildAsync()
                .andThenCompose(animation -> AnimateBuilder.with(qiContext)
                        .withAnimation(animation)
                        .buildAsync())
                .andThenConsume(animate -> {
                    currentAnimFuture = animate.async().run();

                    // Log if it fails to play
                    currentAnimFuture.thenConsume(f -> {
                        if (f.hasError()) {
                            Log.e(TAG, "Animation Error: " + f.getErrorMessage());
                        }
                    });
                 });
    }

    private List<Integer> getAllAnimationResources() {
        List<Integer> resIds = new ArrayList<>();
        Field[] fields = R.raw.class.getFields();
        for (Field field : fields) {
            try {
                // This will grab everything in /raw
                resIds.add(field.getInt(null));
            } catch (Exception e) {
                Log.e(TAG, "Reflection error");
            }
        }
        return resIds;
    }

    @Override
    public void onRobotFocusGained(QiContext context) {
        this.qiContext = context;
        Log.i(TAG, "Robot Focus Gained");
    }

    @Override public void onRobotFocusLost() { this.qiContext = null; }
    @Override public void onRobotFocusRefused(String reason) { Log.e(TAG, "Refused: " + reason); }

    @Override
    protected void onDestroy() {
        QiSDK.unregister(this, this);
        super.onDestroy();
    }
}