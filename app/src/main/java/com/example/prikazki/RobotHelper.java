package com.example.prikazki;

//metodite za audio, animacii i tn

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.List;

public class RobotHelper {
    private QiContext qiContext;
    private void loadTaleData()
    {

    }

    private void playStep()
    {

    }

    private void playAnimationChain()
    {

    }

    public static void runAnimation(QiContext context, int resId, Runnable onComplete) {
        AnimationBuilder.with(context).withResources(resId).buildAsync().andThenConsume(animation ->
                AnimateBuilder.with(context).withAnimation(animation).buildAsync().andThenConsume(animate ->
                        animate.async().run().andThenConsume(ignore -> {
                            if (onComplete != null) onComplete.run();
                        })
                )
        );
    }

}
