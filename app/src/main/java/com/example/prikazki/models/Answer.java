package com.example.prikazki.models;

import org.json.JSONException;
import org.json.JSONObject;

public class Answer {
    public String text;
    public String audioDir;

    public Answer(String text, String audioDir) {
        this.text = text;
        this.audioDir = audioDir;
    }

    public static Answer GetQuestionAnsFromRawData(JSONObject rawData) throws JSONException {
        if (rawData == null)
            return null;

        String text = rawData.getString("text");
        String audioDir = rawData.getString("audio");

        return new Answer(text, audioDir);
    }
}
