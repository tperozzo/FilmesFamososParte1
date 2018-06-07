package com.example.tallesperozzo.filmesfamososparte1.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tallesperozzo.filmesfamososparte1.classes.APIUtils;
import com.example.tallesperozzo.filmesfamososparte1.classes.Movie;
import com.example.tallesperozzo.filmesfamososparte1.classes.MovieAdapter;
import com.perozzo.tmdbexample.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Perozzo on 15/09/2017.
 * Activity to search and list the movies
 */

public class MovieListActivity  extends AppCompatActivity implements MovieAdapter.ListItemClickListener{

    private static final String RV_ITENS_SAVED = "rv_itens_saved";
    private final int MOST_POPULAR = 1;
    private final int TOP_RATED = 2;

    private Context ctx;

    private List<Movie> movieList;

    private MovieAdapter movieAdapter;
    private RecyclerView movieRV;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        this.setTitle(getString(R.string.most_popular));
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        ctx = this;

        if(savedInstanceState == null)
            movieList = new ArrayList<>();
        else
            movieList = (ArrayList<Movie>)savedInstanceState.getSerializable(RV_ITENS_SAVED);

        movieRV = findViewById(R.id.question_rv);

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            movieRV.setLayoutManager(new GridLayoutManager(ctx, 2));
        }
        else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            movieRV.setLayoutManager(new GridLayoutManager(ctx, 4));
        }

        movieAdapter = new MovieAdapter(ctx, movieList, this);


        movieRV.setAdapter(movieAdapter);


        getMovies(MOST_POPULAR);
    }

    public void getMovies(int mode){

        if(mode == MOST_POPULAR)
            this.setTitle(getString(R.string.most_popular));
        else if(mode == TOP_RATED)
            this.setTitle(getString(R.string.top_rated));

        movieAdapter.clear();

        if(isOnline()) {
            MovieAsyncTask task = new MovieAsyncTask();
            task.execute(mode);
        }
        else{
            Toast.makeText(ctx, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
        }

    }

    //Method to verify connection
    private boolean isOnline() {
        try {

            ConnectivityManager cm =
                    (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnectedOrConnecting();
        }
        catch (NullPointerException e){
            e.printStackTrace();
            return false;
        }
    }

    protected void onSaveInstanceState(Bundle state) {
        state.putSerializable(RV_ITENS_SAVED, (Serializable)  movieList);
        super.onSaveInstanceState(state);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT) {
            movieRV.setLayoutManager(new GridLayoutManager(ctx, 2));
        }else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            movieRV.setLayoutManager(new GridLayoutManager(ctx, 4));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.most_popular:
                getMovies(MOST_POPULAR);
                return true;
            case R.id.top_rated:
                getMovies(TOP_RATED);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent i = new Intent(ctx, DetailActivity.class);
        String MOVIE = "MOVIE";
        i.putExtra(MOVIE, movieList.get(clickedItemIndex));
        ctx.startActivity(i);
    }

    //AsyncTask to load questions
    public class MovieAsyncTask extends AsyncTask<Integer,Void,List<Movie>> {

        private ProgressDialog progressDialog;

        @Override
        protected List<Movie> doInBackground(Integer... mode) {
            List<Movie> result = APIUtils.fetchMovies(mode[0]);
            if(result != null) {
                if (!result.isEmpty()) {
                    movieList.addAll(result);

                }
            }
            return result;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(MovieListActivity.this);
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.setCancelable(false);
            progressDialog.show();
            super.onPreExecute();
        }


        @Override
        protected void onPostExecute(List<Movie> data) {
            if(progressDialog.isShowing()){
                progressDialog.dismiss();
            }

            movieAdapter.notifyDataSetChanged();
        }
    }

}
