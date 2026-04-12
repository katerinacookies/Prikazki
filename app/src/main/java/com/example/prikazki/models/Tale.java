package com.example.prikazki.models;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.prikazki.JSONReader;


public class Tale {
    public String name;
    public String authorName;
    public String authorAudio;
    public String id;
    public String group;
    public String soundsPath;
    public String[] pics;
    //dobavqm tozi red, zashtoto ne otkriva this.animations na redove 49-55
    public String[][] animations;

    public Question[] questions;

    public Tale(String name, String authorName, String authorAudio, String id, String group, String soundsPath, String[] pics, String[][] animations, Question[] questions) {
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

        int questionsCount = questions.length;
        this.questions = new Question[questionsCount];
        for (int i = 0; i < questionsCount; ++i) {
            this.questions[i] = questions[i];
        }
    }

    public static Tale GetTaleDataFromId(Context context, String id) throws JSONException {
        Log.d("DEBUG_TAG", "$^$ HEllo world");

        JSONObject JSONRawData = JSONReader.getTaleJSONObject(context, id);

        //Toast.makeText(context, JSONRawData.getString("name"), Toast.LENGTH_LONG).show();


        if (JSONRawData == null)
            return null;

        String name = JSONRawData.getString("name");
        String authorName = JSONRawData.getString("author_name");
        String authorAudio = JSONRawData.getString("author_audio");
        String group = JSONRawData.getString("group");
        String soundsPath = JSONRawData.getString("soundsPath");

        JSONArray picsRaw = JSONRawData.getJSONArray("pics");
        int picsCount = picsRaw.length();
        String[] picsPaths = new String[picsCount];
        for (int i = 0; i < picsCount; i++) {
            picsPaths[i] = picsRaw.getString(i);
        }

        JSONArray animationsRaw = JSONRawData.getJSONArray("animations");


        int animsCount = animationsRaw.length();
        String[][] animations = new String[animsCount][];
        for (int i = 0; i < animsCount; i++) {
            int currAnimsCount = animationsRaw.getJSONArray(i).length();
            animations[i] = new String[currAnimsCount];

            for (int j = 0; j < currAnimsCount; j++) {
                animations[i][j] = animationsRaw.getJSONArray(i).getString(j);
            }
        }

        Question[] questions = Question.GetQuestionsFromTaleId(context, id);

        return new Tale(name, authorName, authorAudio, id, group, soundsPath, picsPaths, animations, questions);
    }
}