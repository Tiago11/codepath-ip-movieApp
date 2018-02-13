package com.codepath.tiago.movieapp.activities;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.codepath.tiago.movieapp.adapters.MovieAdapter;
import com.codepath.tiago.movieapp.R;
import com.codepath.tiago.movieapp.helpers.YoutubeVideoHandlerHelper;
import com.codepath.tiago.movieapp.models.Movie;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerFragment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class MovieActivity extends AppCompatActivity {

    List<Movie> mMovies;
    MovieAdapter mMovieAdapter;
    RecyclerView rvMovies;
    YouTubePlayerFragment youtubeFragment;

    // Key and variable used to store the recycler view state accross orientations changes.
    public final static String RV_STATE_KEY = "recycler_list_state";
    Parcelable mListState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie);

        // Hide Youtube fragment until needed.
        youtubeFragment = (YouTubePlayerFragment) getFragmentManager().findFragmentById(R.id.youtubeFragment);
        getFragmentManager().beginTransaction().hide(youtubeFragment).commit();

        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        mMovies = new ArrayList<Movie>();
        mMovieAdapter = new MovieAdapter(mMovies);
        rvMovies.setAdapter(mMovieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        setupYoutubeFragment();

        // Hit the API and populate the recycler view.
        getMoviesFromApi();
    }

    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save state of the recycler view.
        mListState = rvMovies.getLayoutManager().onSaveInstanceState();
        state.putParcelable(RV_STATE_KEY, mListState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Recover state of the recycler view.
        if (state != null) {
            mListState = state.getParcelable(RV_STATE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mListState != null) {
            rvMovies.getLayoutManager().onRestoreInstanceState(mListState);
        }
    }

    private void getMoviesFromApi() {

        String url = "https://api.themoviedb.org/3/movie/now_playing?api_key=3c736ffd57c5246419aa96e0f3e9aab6";
        AsyncHttpClient client = new AsyncHttpClient();

        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray movieJsonResults = null;

                try {
                    // Parse the response from the API and populate.
                    movieJsonResults = response.getJSONArray("results");
                    mMovies.addAll(Movie.fromJsonArray(movieJsonResults));
                    mMovieAdapter.notifyDataSetChanged();
                    // Restore the state of the recycler view if needed.
                    restoreLayoutManagerPosition();

                    // Get the trailer key for the movies and add them to the models.
                    getTrailerKeyForMoviesFromApi();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    private void getTrailerKeyForMoviesFromApi() {

        // Fetch the trailer key for each movie.
        for (int i = 0; i < mMovies.size(); i++) {

            String url = String.format("https://api.themoviedb.org/3/movie/%s/videos?api_key=3c736ffd57c5246419aa96e0f3e9aab6", String.valueOf(mMovies.get(i).getId()));
            AsyncHttpClient client = new AsyncHttpClient();

            final int k = i;

            client.get(url, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    JSONArray trailerJsonResults = null;

                    try {
                        trailerJsonResults = response.getJSONArray("results");
                        if (trailerJsonResults.length() > 0) {
                            JSONObject trailerInfo = trailerJsonResults.getJSONObject(0);
                            if ("YouTube".equals(trailerInfo.getString("site"))) {
                                String trailerKey = trailerInfo.getString("key");
                                mMovies.get(k).setTrailerKey(trailerKey);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    super.onFailure(statusCode, headers, responseString, throwable);
                }
            });
        }
    }

    // Restore the recycler view state if needed.
    private void restoreLayoutManagerPosition() {
        if (mListState != null) {
            rvMovies.getLayoutManager().onRestoreInstanceState(mListState);
        }
    }

    private void setupYoutubeFragment() {

        mMovieAdapter.ytVideoHandler = new YoutubeVideoHandlerHelper() {
            @Override
            public void initYoutubePlayerFragment(String key) {
                getFragmentManager().beginTransaction().show(youtubeFragment).commit();

                final String k = key;
                youtubeFragment.initialize("AIzaSyDYDQOTuBA7dsv67K6qVlv6Gcb6uWuozW4",
                        new YouTubePlayer.OnInitializedListener() {
                            @Override
                            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {

                                if (b) {
                                    youTubePlayer.play();
                                } else {
                                    youTubePlayer.loadVideo(k);
                                    rvMovies.setVisibility(View.INVISIBLE);
                                }

                                youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
                                    @Override
                                    public void onLoading() {
                                        // To implement.
                                    }

                                    @Override
                                    public void onLoaded(String s) {
                                        // To implement.
                                    }

                                    @Override
                                    public void onAdStarted() {
                                        // To implement.
                                    }

                                    @Override
                                    public void onVideoStarted() {
                                        // To implement.
                                    }

                                    @Override
                                    public void onVideoEnded() {
                                        rvMovies.setVisibility(View.VISIBLE);
                                        getFragmentManager().beginTransaction().hide(youtubeFragment).commit();
                                    }

                                    @Override
                                    public void onError(YouTubePlayer.ErrorReason errorReason) {
                                        // To implement.
                                    }
                                });

                            }

                            @Override
                            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

                            }
                        });
            }
        };

    }
}
