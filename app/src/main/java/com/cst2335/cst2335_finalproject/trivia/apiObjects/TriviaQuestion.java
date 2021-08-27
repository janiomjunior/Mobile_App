package com.cst2335.cst2335_finalproject.trivia.apiObjects;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is used to create objects with all questions collected from the API and implements Serializable interface
 * to allow the activity to send an array of objects from this class to other activity
 */
public class TriviaQuestion implements Serializable {

    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correctAnswer;
    private ArrayList<String> incorrectAnswers;

    /**
     * This constructor creates an object with @param category @param type @param difficulty @param question @param correctAnswer @param incorrectAnswers */
    public TriviaQuestion(String category, String type, String difficulty, String question, String correctAnswer, ArrayList<String> incorrectAnswers){
        this.category = category;
        this.type = type;
        this.difficulty = difficulty;
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.incorrectAnswers = incorrectAnswers;
    }

    /** This method @return the category name */
    public String getCategory() {
        return this.category;
    }
    /** This method @return the question type */
    public String getType() {
        return this.type;
    }
    /** This method @return the question difficulty */
    public String getDifficulty() {
        return this.difficulty;
    }
    /** This method @return the questions content */
    public String getQuestion() {
        return this.question;
    }
    /** This method @return the correct answer */
    public String getCorrectAnswer() {
        return this.correctAnswer;
    }
    /** This method @return the a string array of incorrect answers */
    public ArrayList<String> getIncorrectAnswers() {
        return this.incorrectAnswers;
    }
}