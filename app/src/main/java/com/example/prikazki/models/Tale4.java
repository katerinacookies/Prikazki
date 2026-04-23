package com.example.prikazki.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.prikazki.JSONReader;


public class Tale4 {
    public String name;
    public String authorName;
    public String authorAudio;
    public String id;
    public String group;
    public String soundsPath;
    public String[] pics;
    public String[][] animations;

    public Tale4(String name, String authorName, String authorAudio, String id, String group, String soundsPath, String[] pics, String[][] animations) {
        this.name = name;
        this.authorName = authorName;
        this.authorAudio = authorAudio;
        this.id = id;
        this.group = group;
        this.soundsPath = soundsPath;

        int picsCount = pics.length;
        this.pics = new String[picsCount];
        for (int i = 0; i < picsCount; i++) {
            this.pics[i] = pics[i];
        }

        int animationsStepsCount = animations.length;
        this.animations = new String[animationsStepsCount][];
        for (int i = 0; i < animationsStepsCount; ++i) {
            int currAnimStepsCount = animations[i].length;
            this.animations[i] = new String[currAnimStepsCount];
            for (int j = 0; j < currAnimStepsCount; j++) {
                this.animations[i][j] = animations[i][j];
            }
        }

    }

    public boolean IsValid() throws Exception {
        boolean isValid = true;
        String exceptionMessage = "";

        if (name.contains("$")) {
            exceptionMessage += ("Tale name not valid: " + name + "; ");

            isValid = false;
        }
        if (authorName.contains("$")) {
            exceptionMessage += ("Tale author name not valid: " + authorName + "; ");

            isValid = false;
        }
        if (authorAudio.contains("$")) {
            exceptionMessage += ("Tale author audio not valid: " + authorAudio + "; ");

            isValid = false;
        }
        if (soundsPath.contains("$")) {
            exceptionMessage += ("Tale sound path not valid: " + authorAudio + "; ");

            isValid = false;
        }

        if (isValid)
            return true;
        else
            throw new Exception(exceptionMessage);
    }

    public static Tale4 GetTaleDataFromId(Context context, String id, String partId) throws JSONException {
        Log.d("DEBUG_TAG", "$^$ HEllo world");

        JSONObject JSONRawData = getTaleJSONObject(context, id);

        if (JSONRawData == null)
            return null;

        String name = JSONRawData.getString("name");
        String authorName = JSONRawData.getString("author_name");
        String authorAudio = JSONRawData.getString("author_audio");
        String group = JSONRawData.getString("group");

        JSONArray partsRaw = JSONRawData.getJSONArray("parts");
        JSONObject targetPart = partsRaw.getJSONObject(Integer.parseInt(partId));

        String soundsPath = targetPart.getString("sounds_path");
        JSONArray picsRaw = targetPart.getJSONArray("pics");
        int picsCount = picsRaw.length();
        String[] picsPaths = new String[picsCount];
        for (int i = 0; i < picsCount; i++) {
            picsPaths[i] = picsRaw.getString(i);
        }

        JSONArray animationsRaw = targetPart.getJSONArray("animations");
        int animsCount = animationsRaw.length();
        String[][] animations = new String[animsCount][];
        for (int i = 0; i < animsCount; i++) {
            int currAnimsCount = animationsRaw.getJSONArray(i).length();
            animations[i] = new String[currAnimsCount];

            for (int j = 0; j < currAnimsCount; j++) {
                animations[i][j] = animationsRaw.getJSONArray(i).getString(j);
            }
        }

        return new Tale4(name, authorName, authorAudio, id, group, soundsPath, picsPaths, animations);
    }


    public static JSONObject getTaleJSONObject(Context context, String targTaleId) {
        try {
            String jsonStr = loadJSONStringFromAsset(context, "tales_4.json");

            JSONArray a = new JSONArray(jsonStr);

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

    private static String loadJSONStringFromAsset(Context context, String fileName) throws Exception {
        java.io.InputStream is = context.getAssets().open(fileName);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, "UTF-8");
    }
}