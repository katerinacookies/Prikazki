package com.example.prikazki;

import org.json.JSONException;
import org.json.JSONObject;

public class QuestionAnswer {
    String text;
    String audioDir;

    public QuestionAnswer(String text, String audioDir) {
        this.text = text;
        this.audioDir = audioDir;
    }

    public static QuestionAnswer GetQuestionAnsFromRawData(JSONObject rawData) throws JSONException {
        if (rawData == null)
            return null;

        String text = rawData.getString("text");
        String audioDir = rawData.getString("audio");

        return new QuestionAnswer(text, audioDir);
    }
}
