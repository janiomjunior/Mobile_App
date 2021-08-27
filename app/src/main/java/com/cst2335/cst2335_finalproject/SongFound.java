package com.cst2335.cst2335_finalproject;

/**
 * this class is used to create objects from search of songs informations
 */

public class SongFound {

    String title;
    String songId;
    String artistId;
    String artist;
    long id;

    /**
     * constructor
     * @param titlePassed
     * @param songIdPassed
     * @param artisIdPassded
     * @param artist
     */

    public SongFound(String titlePassed, String songIdPassed, String artisIdPassded, String artist) {
        this.title = titlePassed;
        this.songId = songIdPassed;
        this.artistId = artisIdPassded;
        this.artist = artist;
    }

    /**
     * constructor
     * @param titlePassed
     * @param songIdPassed
     * @param artisIdPassed
     * @param idDbPassed
     */
    public SongFound(String titlePassed, String songIdPassed, String artisIdPassed, String artistPassed, long idDbPassed){
        this.title = titlePassed;
        this.songId = songIdPassed;
        this.artistId = artisIdPassed;
        this.artist = artistPassed;
        this.id = idDbPassed;
    }
    //get methods from instance variables

    public String getTitle() {
        return title;
    }

    public String getSongId() {
        return songId;
    }

    public String getArtistId() {
        return artistId;
    }

    public String getArtist() { return artist; }

    public long getId() { return id; }
}
