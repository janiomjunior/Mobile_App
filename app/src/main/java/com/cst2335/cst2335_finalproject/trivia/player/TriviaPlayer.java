package com.cst2335.cst2335_finalproject.trivia.player;

import com.cst2335.cst2335_finalproject.trivia.apiObjects.TriviaQuestion;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class creates a player object that will save name, score, id and quantity of correctAnswers and incorrectAnswers
 * to save later to the database.
 */
public class TriviaPlayer {

    private String name;
    private int correctAnswers;
    private int incorrectAnswers;
    private int score;
    private long id;
    private HashMap<Integer, String> questionAnswered;

    /**
     * This constructor creates and object with @param name and initializes the
     * correctAnswers, incorrectAnswers, score instances variables to 0
     */
    public TriviaPlayer(String name){
        this.name = name;
        this.correctAnswers = 0;
        this.incorrectAnswers = 0;
        this.score = 0;
        this.questionAnswered = new HashMap<>();
    }

    /**
     * This constructor creates and object with @param name, @param score and @param id
     */
    public TriviaPlayer(String name, int score, long id){
        this.name = name;
        this.score = score;
        this.id = id;
    }

    /**
     * This method returns the name instance variable value
     * @return
     */
    public String getName(){
        return this.name;
    }

    /**
     * This method returns the correctAnswers instance variable value
     * @return
     */
    public int getCorrectAnswers(){
        return this.correctAnswers;
    }

    /**
     * This method returns the incorrectAnswers instance variable value
     * @return
     */
    public int getIncorrectAnswers(){
        return this.incorrectAnswers;
    }

    /**
     * This method returns the score instance variable value
     * @return
     */
    public int getScore(){
        return this.score;
    }

    /**
     * This method returns the id instance variable value
     * @return
     */
    public long getID(){
        return this.id;
    }

    /** This method set the question already answered by the user */
    public HashMap getQuestionAnswered() {
        return this.questionAnswered;
    }

    /** This method set the name of the player object */
    public void setName(String name) {
        this.name = name;
    }

    /** This method set the correctAnswers quantity for the player object */
    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    /** This method set the incorrectAnswers quantity for the player object */
    public void setIncorrectAnswers(int incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }
    /** This method set the score for the player object */
    public void setScore(int score) {
        this.score = score;
    }

    /** This method set the question already answered by the user */
    public void setQuestionAnswered(int questionNumber, String questionAnswered) {
        this.questionAnswered.put(questionNumber, questionAnswered);
    }
}

