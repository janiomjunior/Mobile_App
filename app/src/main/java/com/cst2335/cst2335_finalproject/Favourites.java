package com.cst2335.cst2335_finalproject;

public class Favourites {
    //using long for 64 bit integer
    public long primaryId;

    //string being stored in the class
    public String title;
    public String link;
    public String date;
    public String descriptionofArticle;

    //create the object message
    public Favourites(String title, String link, String date, String descriptionofArticle, long id){
        this.title = title;
        this.link = link;
        this.date = date;
        this.descriptionofArticle = descriptionofArticle;
        primaryId = id;
    }


    public String getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public String getDescriptionofArticle() {
        return descriptionofArticle;
    }

    //returns the title
    public String getTitle() {
        return title;
    }

    //returns id from database
    public long getId() {
        return primaryId;
    }
}
