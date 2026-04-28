package com.example.prikazki;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;


//izbirane na prikazka

public class Tale4PartSelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tale4_part_selection);

        setupStandardButton(R.id.btn_begin, 0);
        setupStandardButton(R.id.btn_middle, 1);
        setupStandardButton(R.id.btn_end, 2);
    }

    private void setupStandardButton(int resId, final int talePartId) {
        Button button = (Button) findViewById(resId);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Tale4PartSelectionActivity.this, TaleActivity4.class);
                intent.putExtra("TALE_PART_ID", Integer.toString(talePartId));
                intent.putExtra("TALE_ID",getIntent().getStringExtra("TALE_ID"));
                startActivity(intent);
            }
        });
    }
}