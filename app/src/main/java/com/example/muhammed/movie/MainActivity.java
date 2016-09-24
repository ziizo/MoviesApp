package com.example.muhammed.movie;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements Callback{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (!isTablet(this)){
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction().add(R.id.container, new PlaceHolderFragment()).commit();
            }
        }
    }



    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onDataRecieved(Movie movie) {
        DetailFragment fragment = (DetailFragment) getSupportFragmentManager().findFragmentById(R.id.container_detail_fragment_tablet);
        fragment.changeData(movie);
    }
}