package com.amwallace.moviedirectory.Data;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amwallace.moviedirectory.Activities.MovieDetailActivity;
import com.amwallace.moviedirectory.Model.Movie;
import com.amwallace.moviedirectory.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieRecyclerViewAdapter extends RecyclerView.Adapter<MovieRecyclerViewAdapter.ViewHolder> {
    private Context context;
    private List<Movie> movieList;

    public MovieRecyclerViewAdapter(Context context, List<Movie> movies) {
        this.context = context;
        movieList = movies;
    }

    @NonNull
    @Override
    public MovieRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_item, parent,false);
        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieRecyclerViewAdapter.ViewHolder holder, int position) {
        //bind view with data
        Movie movie = movieList.get(position);
        //get link to poster image
        String posterLink = movie.getPoster();

        //set movie data into text views and image view
        holder.title.setText(movie.getTitle());
        holder.type.setText(movie.getMovieType());
        //picasso load image from link to poster image view
        Picasso.with(context).load(posterLink).into(holder.poster);
        holder.year.setText(movie.getYear());

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    //inner class for viewholder
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, year, type;
        public ImageView poster;

        public ViewHolder(@NonNull View itemView, final Context ctx) {
            super(itemView);
            context = ctx;
            //set up text & image views
            title = (TextView) itemView.findViewById(R.id.movieTitleTxt);
            year = (TextView) itemView.findViewById(R.id.movieReleaseTxt);
            type = (TextView) itemView.findViewById(R.id.movieCatTxt);
            poster = (ImageView) itemView.findViewById(R.id.movieImg);

            //set up on click listener for whole movie item/row
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Movie movie = movieList.get(getAdapterPosition());
                    Intent intent = new Intent(context, MovieDetailActivity.class);
                    //send movie data to detail activity
                    intent.putExtra("movie", movie);
                    ctx.startActivity(intent);
                }
            });

        }

        @Override
        public void onClick(View v) {

        }
    }
}
