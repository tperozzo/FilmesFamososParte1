package com.example.tallesperozzo.filmesfamososparte1.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tallesperozzo.filmesfamososparte1.classes.APIUtils;
import com.example.tallesperozzo.filmesfamososparte1.classes.Movie;
import com.perozzo.tmdbexample.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Perozzo on 15/09/2017.
 * Activity to show the movies detail
 */

public class DetailActivity extends AppCompatActivity {

    private final static String MOVIE = "MOVIE";
    public final static String UNKNOWN_POSTER_PATH = "Unknown poster path";

    private Movie movie;
    private Context ctx;

    private FrameLayout frameLayout;
    private ImageView poster_image_iv;
    private ProgressBar poster_image_pb;
    private TextView title_tv;
    private TextView vote_average_tv;
    private TextView date_tv;
    private TextView overview_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ctx = this;

        frameLayout = findViewById(R.id.frame_layout);
        poster_image_iv = findViewById(R.id.backdrop_image_iv);
        poster_image_pb = findViewById(R.id.backdrop_image_pb);
        title_tv = findViewById(R.id.title_tv);
        date_tv = findViewById(R.id.date_tv);
        vote_average_tv = findViewById(R.id.vote_average_tv);
        overview_tv = findViewById(R.id.overview_tv);

        final Intent intent = getIntent();

        movie = (Movie) intent.getSerializableExtra(MOVIE);
        if(movie != null) {
            initWidgets();
        }
    }

    //Initialize widgets from interface
    private void initWidgets(){

        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, (float)1));
        }
        else if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, (float)0.5));
        }

        if(movie.getPoster_path().equals(UNKNOWN_POSTER_PATH) || movie.getPoster_path().equals(APIUtils.IMAGE_URL +"/null")) {
            frameLayout.setVisibility(View.GONE);
            poster_image_iv.setVisibility(View.GONE);
            poster_image_pb.setIndeterminate(false);
            poster_image_pb.setVisibility(View.GONE);
        }
        else {
            if(isOnline()) {

                Picasso.get().load(movie.getPoster_path()).fit().into(poster_image_iv, new Callback() {
                    @Override
                    public void onSuccess() {
                        poster_image_pb.setIndeterminate(false);
                        poster_image_pb.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        poster_image_pb.setIndeterminate(false);
                        poster_image_pb.setVisibility(View.GONE);
                    }
                });
            }
            else {
                Toast.makeText(ctx, getString(R.string.no_connection_load_image), Toast.LENGTH_SHORT).show();
                poster_image_pb.setIndeterminate(false);
                poster_image_pb.setVisibility(View.GONE);
                frameLayout.setVisibility(View.GONE);
                poster_image_iv.setVisibility(View.GONE);
            }
        }
        title_tv.setText(movie.getTitle());

        date_tv.setText(movie.getDate());

        vote_average_tv.setText(getString(R.string.vote_average) + " " + String.valueOf(movie.getVote_average()));

        overview_tv.setText(movie.getOverview());

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(newConfig.orientation==Configuration.ORIENTATION_PORTRAIT) {
            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, (float)1));
        }else if(newConfig.orientation==Configuration.ORIENTATION_LANDSCAPE){
            frameLayout.setLayoutParams(new LinearLayout.LayoutParams(0, FrameLayout.LayoutParams.WRAP_CONTENT, (float)0.5));
        }
    }

    //Method to verify connection, necessary to download the image
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
