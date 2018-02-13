package com.codepath.tiago.movieapp.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tiago on 2/12/18.
 */

public class Movie {

    public int id;
    public String posterPath;
    public String backdropPath;
    public String originalTitle;
    public String overview;
    public String trailerKey;

    public Movie() {

    }

    public Movie(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getInt("id");
        this.posterPath = jsonObject.getString("poster_path");
        this.backdropPath = jsonObject.getString("backdrop_path");
        this.originalTitle = jsonObject.getString("original_title");
        this.overview = jsonObject.getString("overview");
    }

    public static List<Movie> fromJsonArray(JSONArray array) {
        List<Movie> results = new ArrayList<Movie>();

        try {
            for (int i = 0; i < array.length(); i++) {
                results.add(new Movie(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return results;
    }

    public int getId() {
        return this.id;
    }

    // Returns the URL to get the portrait orientation image.
    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", this.posterPath);
    }

    // Returns the URL to get the landscape orientation image.
    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w500/%s", this.backdropPath);
    }

    public String getOriginalTitle() {
        return this.originalTitle;
    }

    public String getOverview() {
        return this.overview;
    }

    public String getTrailerKey() {
        return this.trailerKey;
    }

    public void setTrailerKey(String key) {
        this.trailerKey = key;
    }

}
