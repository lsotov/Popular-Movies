package com.example.lsoto.movies;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class MovieFragment extends Fragment {

    public final static String MOVIE_TITLE = "com.example.lsoto.MOVIE_TITLE";
    public final static String MOVIE_SELECTED_MOVIE = "com.example.lsoto.MOVIE_OBJECT";

    private MoviePosterAdapter moviePosterAdapter;

    private void updateMovies(){
        FetchMoviesTask moviesTask = new FetchMoviesTask();
        moviesTask.execute();
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    public MovieFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        moviePosterAdapter = new MoviePosterAdapter(getActivity(), new ArrayList<MoviePoster>());

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_movie, container, false);

        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        //ListView listView = (ListView) rootView.findViewById(R.id.listview_movies);
        if (gridView != null){
            gridView.setAdapter(moviePosterAdapter);

            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    MoviePoster moviePoster = moviePosterAdapter.getItem(i);

                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtra(MOVIE_TITLE, moviePoster.title);
                    intent.putExtra(MOVIE_SELECTED_MOVIE, moviePoster);
                    startActivity(intent);
                }
            });
        }
        return rootView;
    }

    private MoviePoster[] getMoviesDataFromJson(String moviesJsonString, int numMovies) throws JSONException {
        // These are the names of the JSON objects that need to be extracted.
        final String MDB_RESULTS = "results";
        final String MDB_RATING = "vote_average";
        final String MDB_TITLE = "title";
        final String MDB_POSTER = "poster_path";
        final String MDB_OVERVIEW = "overview";
        final String MDB_RELEASE = "release_date";
        final String MDB_ID = "id";



        JSONObject forecastJson = new JSONObject(moviesJsonString);
        JSONArray moviesArray = forecastJson.getJSONArray(MDB_RESULTS);

        MoviePoster[] results = new MoviePoster[moviesArray.length()];

        int id;
        String title;
        String synopsis;
        double rating;
        Date releaseDate;
        String posterPath;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        for (int i = 0; i < moviesArray.length(); i++){
            JSONObject movie = moviesArray.getJSONObject(i);
            id = movie.getInt(MDB_ID);
            title = movie.getString(MDB_TITLE);
            synopsis = movie.getString(MDB_OVERVIEW);
            rating = movie.getDouble(MDB_RATING);
            try {
                releaseDate = sdf.parse(movie.getString(MDB_RELEASE));
            } catch (ParseException e) {
                e.printStackTrace();
                releaseDate = null;
            }
            posterPath = movie.getString(MDB_POSTER);
            results[i] = new MoviePoster(id, title, synopsis, rating, releaseDate, posterPath);
        }

        return results;
    }

    public class FetchMoviesTask extends AsyncTask<Void, Void, MoviePoster[]>{
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        @Override
        protected MoviePoster[] doInBackground(Void... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;

            String format = "json";
            String units = "metric";
            int numMovies = 7;

            try {
                String sortOrder = PreferenceManager.getDefaultSharedPreferences(getActivity())
                        .getString(getString(R.string.pref_sort_key), getString(R.string.pref_sort_default_value));

                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + sortOrder;
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DATABASE_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    moviesJsonStr = null;
                }
                moviesJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            try {
                return getMoviesDataFromJson(moviesJsonStr, numMovies);
            } catch (JSONException e){
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(MoviePoster[] result) {
            if (result != null){
                moviePosterAdapter.clear();
                for (MoviePoster movieStr : result){
                    moviePosterAdapter.add(movieStr);
                }
            }
        }
    }

}
