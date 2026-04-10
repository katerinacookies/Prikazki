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
    public static JSONObject getTaleJSONObject(Context context, int targTaleId) {
        try {
            JSONObject json = loadJSONFromAsset(context, "example.json");

            JSONArray a = new JSONArray(json);

            for (int i = 0; i < a.length(); i++) {
                JSONObject tale = a.getJSONObject(i);

                int currTaleId = Integer.parseInt(tale.getString("id"));

                if (currTaleId == targTaleId) {
                    return tale;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static JSONObject getQuestionsJSONObject(Context context, int taleId) {
        try {
            JSONObject json = loadJSONFromAsset(context, "example_questions.json");

            JSONArray a = new JSONArray(json);

            for (int i = 0; i < a.length(); i++) {
                JSONObject questions = a.getJSONObject(i);

                int currTaleId = questions.getInt("id");

                if (currTaleId == taleId) {
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