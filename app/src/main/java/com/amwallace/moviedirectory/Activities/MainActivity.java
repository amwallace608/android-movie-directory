package com.amwallace.moviedirectory.Activities;

import android.os.Bundle;

import com.amwallace.moviedirectory.Data.MovieRecyclerViewAdapter;
import com.amwallace.moviedirectory.Model.Movie;
import com.amwallace.moviedirectory.R;
import com.amwallace.moviedirectory.Util.Prefs;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MovieRecyclerViewAdapter movieRecyclerViewAdapter;
    private List<Movie> movieList;
    private RequestQueue requestQueue;
    private AlertDialog.Builder searchDialogBuilder;
    private AlertDialog searchDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //new request queue
        requestQueue = Volley.newRequestQueue(this);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //search function alert dialog
                searchAlertDialog();
            }
        });
        //recycler view setup
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Prefs prefs = new Prefs(MainActivity.this);
        movieList = new ArrayList<>();

        //get search from shared prefs

        movieList = getMovies(prefs.getSearch());

        movieRecyclerViewAdapter = new MovieRecyclerViewAdapter(this, movieList);
        recyclerView.setAdapter(movieRecyclerViewAdapter);
        movieRecyclerViewAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //if Search option selected, show search alert dialog
        if (id == R.id.new_search) {
            searchAlertDialog();
            //return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //show search alert dialog
    public void searchAlertDialog() {
        //setup search dialog alert builder
        searchDialogBuilder = new AlertDialog.Builder(this);
        //inflate searchdialog view
        View view = getLayoutInflater().inflate(R.layout.searchdialog,null  );
        //set up search edit text and submit button
        final EditText searchEdt = (EditText) view.findViewById(R.id.searchEdtTxt);
        Button submitBtn = (Button) view.findViewById(R.id.submitBtn);

        //create  view and show
        searchDialogBuilder.setView(view);
        searchDialog = searchDialogBuilder.create();
        searchDialog.show();
        //submit button onclick listener to update search in prefs
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Prefs prefs = new Prefs(MainActivity.this);
                //if search input is not empty
                if(!searchEdt.getText().toString().isEmpty()){
                    //set prefs search term to search edit text input
                    prefs.setSearch(searchEdt.getText().toString());
                    //clear movie list
                    movieList.clear();
                    //getMovies for new search term
                    getMovies(searchEdt.getText().toString());
                    //notify data for recycler menu has changed
                    movieRecyclerViewAdapter.notifyDataSetChanged();
                }
                //if search input is empty, dismiss dialog w/out updating search term
                searchDialog.dismiss();
            }
        });
    }

    //get movies according to search term
    public List<Movie> getMovies(String searchTerm){
        //clear current list of movies
        movieList.clear();
        //generate URL string for search term with API address and key
        String searchUrl = getApplicationContext().getString(R.string.api_url_search) + searchTerm
                + getApplicationContext().getString(R.string.api_key);
        Log.d("SEARCH URL:", searchUrl);
        //json object request to get search results from api
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, searchUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    //get JSONArray of all movies found in search
                    JSONArray moviesArray = response.getJSONArray("Search");

                    for(int i = 0; i < moviesArray.length(); i++){
                        //get movie object at index i
                        JSONObject movieObj = moviesArray.getJSONObject(i);
                        //get movie data from Json object
                        Movie movie = new Movie(movieObj.getString("Title"),
                                "Year: " + movieObj.getString("Year"),
                                movieObj.getString("imdbID"),
                                movieObj.getString("Poster"),
                                "Type: " + movieObj.getString("Type"));

                        Log.d("Movie Result ------- ",
                                movie.getTitle() + " " + movie.getYear());
                        //add movie to list
                        movieList.add(movie);
                    }
                    //make sure recycler view displays movies
                    movieRecyclerViewAdapter.notifyDataSetChanged();
                } catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //add request to queue
        requestQueue.add(jsonObjectRequest);
        //return list of movies from search
        return movieList;
    }
}
