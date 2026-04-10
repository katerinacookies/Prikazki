package com.example.prikazki;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Question {
    int taleId;
    String text;
    String audioDir;
    QuestionAnswer[] answers;
    int rightAnswerId;

    public Question(int taleId, String text, String audioDir, QuestionAnswer[] answers, int rightAnswerId) {
        this.taleId = taleId;
        this.text = text;
        this.audioDir = audioDir;
        this.answers = answers;
        this.rightAnswerId = rightAnswerId;
    }

    public static Question GetQuestionFromJSONObject(JSONObject rawData) throws JSONException {
        if (rawData == null)
            return null;

        int taleId = rawData.getInt("tale_id");
        String questionText = rawData.getString("text");
        String audioDir = rawData.getString("audio_dir");

        JSONArray rawAnswers = rawData.getJSONArray("answer");
        int answersCount = rawAnswers.length();
        QuestionAnswer[] questionAnswers = new QuestionAnswer[answersCount];

        for (int i = 0; i < answersCount; i++) {
            questionAnswers[i] = QuestionAnswer.GetQuestionAnsFromRawData(rawAnswers.getJSONObject(i));
        }

        int rightAnswerId = rawData.getInt("right_answer_id");

        return new Question(taleId, questionText, audioDir, questionAnswers, rightAnswerId);
    }

    public static Question GetQuestionFromTaleId(Context context, int id) throws  JSONException {
        JSONObject JSONRawData = JSONReader.getTaleJSONData(context, id);

        if (JSONRawData == null)
            return null;

        int taleId = JSONRawData.getInt("tale_id");
        String questionText = JSONRawData.getString("text");
        String audioDir = JSONRawData.getString("audio_dir");

        JSONArray rawAnswers = JSONRawData.getJSONArray("answer");
        int answersCount = rawAnswers.length();
        QuestionAnswer[] questionAnswers = new QuestionAnswer[answersCount];

        for (int i = 0; i < answersCount; i++) {
            questionAnswers[i] = QuestionAnswer.GetQuestionAnsFromRawData(rawAnswers.getJSONObject(i));
        }

        int rightAnswerId = JSONRawData.getInt("right_answer_id");

        return new Question(taleId, questionText, audioDir, questionAnswers, rightAnswerId);
    }
}
