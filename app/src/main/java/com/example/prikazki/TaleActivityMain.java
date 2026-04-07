package com.example.prikazki;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;


//logikata za prikazkite za 1, 2 i 3ta grupa
public class TaleActivityMain extends AppCompatActivity{
    private JSONObject taleData;
    private JSONArray stepsArray;

    @Override
    public void onCreate(Bundle SavedInstanceState){
        super.onCreate(SavedInstanceState);
        setContentView(R.layout.activity_main_menu); //or activity_main?

        LinearLayout container = (LinearLayout) findViewById(R.id.buttonContainer);
        listTalesButtons(this, container);
    }

    public void listTalesButtons(Context context, LinearLayout container){
        LayoutInflater inflater = LayoutInflater.from(context);

        try{
            // JSON->string
            InputStream is = context.getAssets().open("TESTtales.json");
            byte[] buffer = new byte[is.available()];
            is.read(buffer);
            is.close();
            String json = new String(buffer, StandardCharsets.UTF_8);

            // string->array
            JSONArray array = new JSONArray(json);

            // array->buttons
            for(int i=0;i<array.length();i++){
                JSONObject tale = array.getJSONObject(i);
                String title = tale.getString("name");

                Button taleButton = (Button) inflater.inflate(R.layout.item_button, container, false);
                taleButton.setText(title);

                taleButton.setOnClickListener(v -> {
                    Toast.makeText(context, "Clicked: "+title, Toast.LENGTH_SHORT).show();
                });
                container.addView(taleButton);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
