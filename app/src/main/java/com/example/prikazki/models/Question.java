package com.example.prikazki.models;

import android.content.Context;

import com.example.prikazki.JSONReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Question {
    public String taleId;
    public String text;
    public String audioDir;
    public Answer[] answers;
    public int rightAnswerId;

    public Question(String taleId, String text, String audioDir, Answer[] answers, int rightAnswerId) {
        this.taleId = taleId;
        this.text = text;
        this.audioDir = audioDir;
        this.answers = answers;
        this.rightAnswerId = rightAnswerId;
    }

    public static Question GetQuestionFromJSONObject(JSONObject rawData) throws JSONException {
        if (rawData == null)
            return null;

        String taleId = rawData.getString("tale_id");
        String questionText = rawData.getString("text");
        String audioDir = rawData.getString("audio_dir");

        JSONArray rawAnswers = rawData.getJSONArray("answer");
        int answersCount = rawAnswers.length();
        Answer[] answers = new Answer[answersCount];

        for (int i = 0; i < answersCount; i++) {
            answers[i] = Answer.ConvertToAnswer(rawAnswers.getJSONObject(i));
        }

        int rightAnswerId = rawData.getInt("right_answer_id");

        return new Question(taleId, questionText, audioDir, answers, rightAnswerId);
    }

    public static Question[] GetQuestionsFromTaleId(Context context, String id) throws JSONException {
        JSONObject questionData = JSONReader.getQuestionsJSONObject(context, id);

        if (questionData == null) return null;

        String taleId = questionData.getString("tale_id");
        JSONArray questionsArray = questionData.getJSONArray("questions");

        int questionsCount = questionsArray.length();
        Question[] questions = new Question[questionsCount];

        for (int i = 0; i < questionsCount; i++) {
            JSONObject questionObject = questionsArray.getJSONObject(i);

            String questionText = questionObject.getString("text");
            String audioDir = questionObject.getString("audio_dir");
            int rightAnswerId = questionObject.getInt("right_answer_id");

            JSONArray rawAnswers = questionObject.getJSONArray("answers");

            int answersCount = rawAnswers.length();
            Answer[] answers = new Answer[answersCount];

            for (int j = 0; j < answersCount; j++) {
                answers[j] = Answer.ConvertToAnswer(rawAnswers.getJSONObject(j));
            }

            questions[i] = new Question(taleId, questionText, audioDir, answers, rightAnswerId);
        }

        return questions;
    }
}
