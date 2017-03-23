package com.example.lsoto.movies;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by lsoto on 3/22/2017.
 */

public class MoviePoster implements Serializable {
    int id;
    String title;
    String synopsis;
    double rating;
    Date releaseDate;
    String posterPath;

    private String basePath = "http://image.tmdb.org/t/p/";
    private String imageSize = "w500";

    public MoviePoster(int pId, String pTitle, String pSynopsis, double pRating, Date pReleaseDate, String vPoster){
        this.id = pId;
        this.title = pTitle;
        this.synopsis = pSynopsis;
        this.rating = pRating;
        this.releaseDate = pReleaseDate;
        this.posterPath = vPoster;
    }

    public String GetMoviePoster(){
        return this.basePath + this.imageSize + this.posterPath;
    }
}
