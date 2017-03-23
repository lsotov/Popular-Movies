package com.example.lsoto.movies;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {


    public DetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Intent intent = getActivity().getIntent();
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        if (intent != null && intent.hasExtra(MovieFragment.MOVIE_SELECTED_MOVIE) && intent.hasExtra(MovieFragment.MOVIE_TITLE)){
            MoviePoster moviePoster = (MoviePoster) intent.getSerializableExtra(MovieFragment.MOVIE_SELECTED_MOVIE);

            ((TextView)rootView.findViewById(R.id.movie_detail_name)).setText(moviePoster.title);

            ((TextView)rootView.findViewById(R.id.movie_detail_overview)).setText(moviePoster.synopsis);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            ((TextView)rootView.findViewById(R.id.movie_detail_release_date)).setText(sdf.format(moviePoster.releaseDate));

            String rating = String.valueOf(moviePoster.rating) + "/10";
            ((TextView)rootView.findViewById(R.id.movie_detail_rating)).setText(rating);

            ImageView posterView = (ImageView)rootView.findViewById(R.id.movie_detail_poster);
            Picasso.with(getContext()).load(moviePoster.GetMoviePoster()).into(posterView);;
        }

        return rootView;
    }

}
