package com.cst2335.cst2335_finalproject.trivia.builder;

/**
 * This class is used to build the custom API url based on the user choice like quantity of questions,
 * difficulty, questions category and type. After build the URL, the app will used it to load the questions.
 */
public class URLBuilder {
    private final String baseAPIURL = "https://opentdb.com/api.php?";
    private int quantity;
    private int category;
    private final String[] difficulty = {"Any", "Easy", "Medium", "Hard"};
    private int difficultyChoice;
    private final String[] typeOfQuestion = {"Any", "Multiple Choice", "True/False"};
    private int typeChoice;

    /**
     * Default constructor to initialize the default variable values if the user do not choose all or some of them
     */
    public URLBuilder() {
        this.quantity = 10;
        this.category = 0;
        this.difficultyChoice = 0;
        this.typeChoice = 0;
    }

    /** This method sets the quantity variable by passing the @param quantity value to the instance variable */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    /** This method sets the category variable by passing the @param category value to the instance variable */
    public void setCategory(int category) {
        this.category = category;
    }

    /** This method sets the type of questions variable by passing the @param typeChoice value to the instance variable */
    public void setType(int typeChoice) {
        //Compares if typeChoice value matches with the view id of multiple choice chosen by the user in the UI
        if(typeChoice == 2131230977){
            //Multiple choice
            //Sets the typeChoice to 1
            this.typeChoice = 1;
        }
        //Compares if typeChoice value matches with the view id of True/False choice chosen by the user in the UI
        else if(typeChoice == 2131230813){
            //True or False (boolean)
            //Sets the typeChoice to 1
            this.typeChoice = 2;
        }
        //If the user didn't choose the type of question or if selected any type, the variable typeChoice will receive the default type
        else{
            //Any type of questions
            this.typeChoice = 0;
        }

    }

    /**
     * This method sets difficulty variable by passing @param difficultyChoice value to the instance variable */
    public void setDifficulty(int difficultyChoice) {
        this.difficultyChoice = difficultyChoice;
    }

    /** This method builds and @return the complete URL as a String to be used to load the questions based on the user's choice */
    public String getURL(){
        //Creating and initializing a string builder object with the base URL from the API that can be changed depending on the user's choice
        StringBuilder builder = new StringBuilder(this.baseAPIURL);

        builder.append("amount=")
                .append(this.quantity);
        if(this.category > 0){
            builder.append("&");
            builder.append("category=")
                    .append(this.category);
        }
        if(this.difficultyChoice > 0 && this.difficultyChoice <= 3) {
            builder.append("&");
            builder.append("difficulty=")
                    .append(this.difficulty[difficultyChoice].toLowerCase());
        }
        if(this.typeChoice >= 1 && this.typeChoice <= 2) {
            builder.append("&");
            if(this.typeChoice == 1){
                builder.append("type=" + "multiple");
            }else if(this.typeChoice == 2) {
                builder.append("type=" + "boolean");
            }

        }
        return builder.toString();
    }//End of getURL
}//End of URLBuilder