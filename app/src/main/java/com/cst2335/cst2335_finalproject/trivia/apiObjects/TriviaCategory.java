package com.cst2335.cst2335_finalproject.trivia.apiObjects;

/**
 * This class is uses to create objects with all categories collected from the API
 */
public class TriviaCategory {

    private String id;
    private String name;

    /**
     * This constructor receives id and name of the category to create the object
     * @param id
     * @param name
     */
    public TriviaCategory(String id, String name){
        this.id = id;
        this.name = name;
    }

    /** This method @return the category id */
    public String getId() {
        return this.id;
    }

    /** This method @return the category name */
    public String getName() {
        return this.name;
    }

    /** This method set the category id */
    public void setId(String id) {
        this.id = id;
    }

    /** This method set the category name */
    public void setName(String name) {
        this.name = name;
    }
}
