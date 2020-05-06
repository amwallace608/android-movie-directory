package com.amwallace.moviedirectory.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amwallace.moviedirectory.Model.Movie;
import com.amwallace.moviedirectory.R;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MovieDetailActivity extends AppCompatActivity {
    private Movie movie;
    private ImageView movieImage;
    private TextView title, director, year, runTime, category, writer, actors, plot,
            rating, boxOffice;
    private RequestQueue requestQueue;
    private String movieId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        //instantiate volley request queue
        requestQueue = Volley.newRequestQueue(this);
        //get movie data from intent extras
        movie = (Movie) getIntent().getSerializableExtra("movie");
        movieId = movie.getImdbId();

        //setup TextViews/ImageView/UI
        setUpUI();
        //Get Movie Details from IMBD ID, populate view
        getMovieDetails(movieId);
    }

    private void setUpUI() {
        //TextViews
        title = (TextView) findViewById(R.id.detTitleTxt);
        director = (TextView) findViewById(R.id.detDirectorTxt);
        year = (TextView) findViewById(R.id.detReleaseTxt);
        runTime = (TextView) findViewById(R.id.detRuntimeTxt);
        category = (TextView) findViewById(R.id.detCatTxt);
        writer = (TextView) findViewById(R.id.detWritersTxt);
        actors = (TextView) findViewById(R.id.detActorsTxt);
        plot = (TextView) findViewById(R.id.detPlotTxt);
        rating = (TextView) findViewById(R.id.detRatingTxt);
        boxOffice = (TextView) findViewById(R.id.detBoxOfficeTxt);
        //ImageView
        movieImage = (ImageView) findViewById(R.id.detMovieImg);

    }

    private void getMovieDetails(String id) {
        //setup api search URL for passed IMDB id of movie
        String imdbSearchUrl = getApplicationContext().getString(R.string.api_url_imdbid)
                + id + getApplicationContext().getString(R.string.api_key);
        Log.d("IMDB SEARCH URL:", imdbSearchUrl);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                imdbSearchUrl, null, new Response.Listener<JSONObject>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(JSONObject response) {

                try {
                    if(response.has("Ratings")){
                        JSONArray ratings = response.getJSONArray("Ratings");
                        String source = null;
                        String value = null;
                        //if items are inside ratings array
                        if(ratings.length() > 0){
                            //get rating item (second to last, or only rating in array)
                            JSONObject mRatings = ratings.getJSONObject((ratings.length() - 1));
                            source = mRatings.getString("Source");
                            value = mRatings.getString("Value");
                            //set rating text to display retrieved rating data
                            rating.setText(source + " " + value);
                        }
                        else{
                            //set rating text to display not applicable
                            rating.setText("Ratings: N/A");
                        }
                        //set other movie details to display w/ textViews
                        title.setText(response.getString("Title"));
                        year.setText("Released: " + response.getString("Year"));
                        director.setText("Director: " + response.getString("Director"));
                        runTime.setText("Runtime: " +response.getString("Runtime"));
                        category.setText("Genre: " +response.getString("Genre"));
                        writer.setText("Writers: " + response.getString("Writer"));
                        actors.setText("Actors: " + response.getString("Actors"));
                        plot.setText("Plot: " + response.getString("Plot"));
                        boxOffice.setText("BoxOffice: " + response.getString("BoxOffice"));
                        //display movie poster w/ picasso and imageView
                        String posterlink = response.getString("Poster");
                        Picasso.with(getApplicationContext()).load(posterlink).into(movieImage);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d("Error: ", error.getMessage());
            }
        });
        //add request to queue
        requestQueue.add(jsonObjectRequest);
    }

}
