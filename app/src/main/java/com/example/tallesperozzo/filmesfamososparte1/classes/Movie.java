package com.example.tallesperozzo.filmesfamososparte1.classes;

import java.io.Serializable;

/**
 * Created by Perozzo on 15/09/2017.
 * Object Movie
 */

public class Movie implements Serializable {

    private final String title;
    private final String date;
    private final String poster_path;
    private final String vote_average;
    private final String overview;

    public Movie(String title, String date, String poster_path, String vote_average, String overview) {
        this.title = title;
        this.date = date;
        this.poster_path = poster_path;
        this.vote_average = vote_average;
        this.overview = overview;
    }

    public String getTitle() {
        return title;
    }

    public String getDate() {
        return date;
    }

    public String getPoster_path() {
        return poster_path;
    }

    public String getVote_average() {
        return vote_average;
    }

    public String getOverview() {
        return overview;
    }
}
