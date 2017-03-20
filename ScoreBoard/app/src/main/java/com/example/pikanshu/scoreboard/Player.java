package com.example.pikanshu.scoreboard;

import java.io.Serializable;

/**
 * Created by pika on 12/3/17.
 */

public class Player implements Serializable {
    public Long _id; // for cupboard
    private int id;
    private String name;
    private String imageURL;
    private int totalScore;
    private String description;
    private int matchesPlayed;
    private String country;
    private int favourite = 0;

    // constructor
    public Player() {
    }
    // constructor
    public Player(int id, String name, String imageURL, int totalScore, String description, int matchesPlayed, String country) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
        this.totalScore = totalScore;
        this.description = description;
        this.matchesPlayed = matchesPlayed;
        this.country = country;
    }

    // getter and setter methods

    public void setFavourite(int favourite) {
        this.favourite = favourite;
    }

    public int isFavourite() {

        return this.favourite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getMatchesPlayed() {
        return matchesPlayed;
    }

    public void setMatchesPlayed(int matchesPlayed) {
        this.matchesPlayed = matchesPlayed;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }
}
