package com.example.lsoto.movies;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by lsoto on 3/22/2017.
 */

public class MoviePosterAdapter extends ArrayAdapter<MoviePoster> {

    public MoviePosterAdapter(Activity context, List<MoviePoster> moviePosters){
        super(context, 0, moviePosters);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MoviePoster moviePoster = getItem(position);
        View rootView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);

        ImageView iconView = (ImageView) rootView.findViewById(R.id.list_item_movie_imageview);
        //iconView.setImageResource(moviePoster.moviePosterPath);
        Picasso.with(getContext()).load(moviePoster.GetMoviePoster()).into(iconView);

        return rootView;
    }
}
