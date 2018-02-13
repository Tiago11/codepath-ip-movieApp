package com.codepath.tiago.movieapp.adapters;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.codepath.tiago.movieapp.R;
import com.codepath.tiago.movieapp.helpers.DeviceDimensionsHelper;
import com.codepath.tiago.movieapp.helpers.YoutubeVideoHandlerHelper;
import com.codepath.tiago.movieapp.models.Movie;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by tiago on 2/12/18.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private List<Movie> mMovies;
    private Context mContext;

    public YoutubeVideoHandlerHelper ytVideoHandler;

    public MovieAdapter(List<Movie> movies) {
        this.mMovies = movies;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);

        View movieView = inflater.inflate(R.layout.item_movie, parent, false);
        ViewHolder viewHolder = new ViewHolder(movieView);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final Movie movie = mMovies.get(position);

        int orientation = mContext.getResources().getConfiguration().orientation;
        String imagePath = "";
        int imageWidth = 0;
        int imagePlaceholder = 0;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            imagePath = movie.getPosterPath();
            imageWidth = (int) (0.4* DeviceDimensionsHelper.getDisplayWidth(mContext));
        } else if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            imagePath = movie.getBackdropPath();
            imageWidth = (int) (0.6*DeviceDimensionsHelper.getDisplayWidth(mContext));

            if (holder.ibPlayTrailer != null) {
                setupPlayTrailerButtonListener(holder, movie);
            }

        }

        holder.ivMovieImage.setImageResource(0); // To avoid using the cached image before loading the next one.
        Picasso.with(mContext).load(imagePath).resize(imageWidth, 0).into(holder.ivMovieImage);

        holder.tvTitle.setText(movie.getOriginalTitle());
        holder.tvOverview.setText(movie.getOverview());
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    // Attach the onClick event on the trailer play button.
    private void setupPlayTrailerButtonListener(ViewHolder holder, Movie movie) {
        final Movie m = movie;

        holder.ibPlayTrailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ytVideoHandler != null) {
                    ytVideoHandler.initYoutubePlayerFragment(m.getTrailerKey());
                }
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivMovieImage;
        TextView tvTitle;
        TextView tvOverview;
        ImageButton ibPlayTrailer;

        public ViewHolder(View itemView) {
            super(itemView);

            ivMovieImage = (ImageView) itemView.findViewById(R.id.ivMovieImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            tvOverview = (TextView) itemView.findViewById(R.id.tvOverview);
            ibPlayTrailer = (ImageButton) itemView.findViewById(R.id.ibPlayTrailer);

        }
    }

}
