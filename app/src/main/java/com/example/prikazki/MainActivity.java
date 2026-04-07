package com.example.prikazki;

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
import android.content.Intent;

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

import java.util.ArrayList;
import java.util.List;


//izbirane na prikazka

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setupStandardButton(R.id.btn_proj1, 1);
        setupStandardButton(R.id.btn_proj2, 2);
        setupStandardButton(R.id.btn_proj3, 3);

        //za 4ta grupa
        Button btn4 = (Button) findViewById(R.id.btn_proj4);
        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TaleActivity4.class);
                intent.putExtra("GROUP_ID", 4);
                startActivity(intent);
            }
        });
    }

    //za 1-3 grupa
    private void setupStandardButton(int resId, final int groupId) {
        Button button = (Button) findViewById(resId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // GO TO SELECTION SCREEN FIRST
                Intent intent = new Intent(MainActivity.this, TaleSelectionActivity.class);
                intent.putExtra("GROUP_ID", groupId);
                startActivity(intent);
            }
        });
    }
}