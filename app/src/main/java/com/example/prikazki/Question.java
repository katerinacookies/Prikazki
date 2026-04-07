package com.example.prikazki;

public class Question {
    int id;
    String text;
    String audioDir;
    String[] answers;
    int rightAnswerId;

    public Question(int id, String text, String audioDir, String[] answers, int rightAnswerId) {
        this.id = id;
        this.text = text;
        this.audioDir = audioDir;
        this.answers = answers;
        this.rightAnswerId = rightAnswerId;
    }
}
