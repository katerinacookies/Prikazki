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

public class JSONReader {
    public static JSONObject getTaleJSONObject(Context context, String targTaleId) {
        try {
            JSONObject json = loadJSONFromAsset(context, "example.json");

            JSONArray a = new JSONArray(json);

            Toast.makeText(context, "Broi na prikazki: " + a.length(), Toast.LENGTH_LONG).show();

            for (int i = 0; i < a.length(); i++) {
                JSONObject tale = a.getJSONObject(i);

                String currTaleId = tale.getString("id");

                if (currTaleId.equals(targTaleId)) {
                    return tale;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

        return null;
    }

    public static JSONObject getQuestionsJSONObject(Context context, String taleId) {
        try {
            JSONObject json = loadJSONFromAsset(context, "example_questions.json");

            JSONArray a = new JSONArray(json);

            for (int i = 0; i < a.length(); i++) {
                JSONObject questions = a.getJSONObject(i);

                String currTaleId = questions.getString("id");

                if (currTaleId.equals(taleId)) {
                    return questions;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private static JSONObject loadJSONFromAsset(Context context, String fileName) throws Exception {
        java.io.InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new JSONObject(new String(buffer, "UTF-8"));
    }
}