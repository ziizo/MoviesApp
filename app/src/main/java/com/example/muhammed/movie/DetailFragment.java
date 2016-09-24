package com.example.muhammed.movie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DetailFragment extends Fragment {

    DBConnection dbConnection;
    Button favourite;

    ProgressDialog dialog;
    Intent intent5;
    String ID;
    Button trailers,reviews;
    Callback callback;
    private TextView originalTitle;
    private ImageView poster;
    private TextView overview;
    private TextView date;
    private TextView rate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        originalTitle = (TextView) rootView.findViewById(R.id.original_title);
        poster= (ImageView) rootView.findViewById(R.id.poster);
        overview= (TextView) rootView.findViewById(R.id.overview);
        date= (TextView) rootView.findViewById(R.id.date);
        rate = (TextView) rootView.findViewById(R.id.rate);
        favourite= (Button) rootView.findViewById(R.id.favourite);
        ID = intent.getStringExtra("Id").toString();
        dbConnection = new DBConnection(getActivity());
        if(dbConnection.isFavorite(ID)){
            favourite.setText("Remove Favourite");
            Log.v("7amada","d5al eno ykon remove favourite");

        }else{
            favourite.setText("Add Favourite");
            Log.v("7amada","d5al eno ykon add favourite");

        }


        originalTitle.setText(intent.getStringExtra("OriginalTitle").toString());
        String url ="http://image.tmdb.org/t/p/w185/"+intent.getStringExtra("PosterPath").toString();
        Picasso.with(getActivity()).load(url).into(poster);
        overview.setText(intent.getStringExtra("OverView").toString());
        rate.setText(intent.getStringExtra("Rate").toString());
        date.setText(intent.getStringExtra("Date").toString());
//            http://api.themoviedb.org/3/movie/246655/videos?api_key=5945d52b08ed894690d920432fa4f0f0
        trailers= (Button) rootView.findViewById(R.id.trailers);
        try {
            if(isOnline()){
                trailers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FetchTrailers fetchTrailers = new FetchTrailers();
                        fetchTrailers.execute(ID,"videos","key");

                    }
                });}
            else{
                Toast.makeText(getActivity(), "No Network Access ...!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        reviews= (Button) rootView.findViewById(R.id.reviews);
        try {
            if(isOnline()) {
                reviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FetchTrailers fetchTrailers = new FetchTrailers();
                        fetchTrailers.execute(ID, "reviews", "content");
                    }
                });
            }else{
                Toast.makeText(getActivity(), "No Network Access ...!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//            if(favourite.getText().equals("Add Favourite")){
        favourite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favourite.getText().equals("Add Favourite")) {
                    dbConnection = new DBConnection(getActivity());

                    dbConnection.addMovie(ID, getActivity().getIntent().getStringExtra("PosterPath").toString(),
                            getActivity().getIntent().getStringExtra("Date").toString(),
                            getActivity().getIntent().getStringExtra("Rate").toString(),
                            getActivity().getIntent().getStringExtra("OverView").toString(),
                            getActivity().getIntent().getStringExtra("OriginalTitle").toString());
                    favourite.setText("Remove Favourite");
                }else if(favourite.getText().equals("Remove Favourite")){
                    dbConnection.removeMovie(ID);
                    favourite.setText("Add Favourite");
                }
                ;
            }

        });
//                favourite.setText("Remove Favourite");
//            }
//            else if(favourite.getText().equals("Remove Favourite")){
//
//            }


//            TextView textView = (TextView) rootView.findViewById(R.id.txt);
//
//            mForecastStr = intent.getStringExtra(Intent.EXTRA_TEXT);
//            textView.setText(mForecastStr);
        return rootView;
    }
    public boolean isOnline() throws IOException, InterruptedException {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    ArrayList<String> keys = new ArrayList<String>();

    public void changeData(Movie movie) {
        dbConnection = new DBConnection(getActivity());
        if(dbConnection.isFavorite(ID)){
            favourite.setText("Remove Favourite");
            Log.v("7amada","d5al eno ykon remove favourite");

        }else{
            favourite.setText("Add Favourite");
            Log.v("7amada","d5al eno ykon add favourite");

        }


        originalTitle.setText(movie.getOriginalTitle());
        String url ="http://image.tmdb.org/t/p/w185/"+movie.getImgPath();
        Picasso.with(getActivity()).load(url).into(poster);
        overview.setText(movie.getOverView());
        rate.setText(movie.getRate());
        date.setText(movie.getDate());

        try {
            if(isOnline()){
                trailers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FetchTrailers fetchTrailers = new FetchTrailers();
                        fetchTrailers.execute(ID,"videos","key");

                    }
                });}
            else{
                Toast.makeText(getActivity(), "No Network Access ...!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            if(isOnline()) {
                reviews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FetchTrailers fetchTrailers = new FetchTrailers();
                        fetchTrailers.execute(ID, "reviews", "content");
                    }
                });
            }else{
                Toast.makeText(getActivity(), "No Network Access ...!",
                        Toast.LENGTH_LONG).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public class FetchTrailers extends AsyncTask<String, Void, ArrayList<String>> {
        private final String LOG_TAG = FetchTrailers.class.getSimpleName();


        private ArrayList<String> GetKeys(String JsonStr,String data)
                throws JSONException {
            Log.v("GetKeys","d5al el method");
            keys.clear();
            final String OWM_RESULTS = "results";
//                final String OWM_KEY = "key";
            JSONObject JsonData = new JSONObject(JsonStr);
            JSONArray ResultsArray = JsonData.getJSONArray(OWM_RESULTS);
            for (int i = 0; i < ResultsArray.length(); i++){
                String Key;
                JSONObject object = ResultsArray.getJSONObject(i);
                Key =object.getString(data);
                keys.add(Key);
                //Log.v("7mada"+i,Key);
            }
            Log.v("GetKeys","5rag mn el loop");


            return keys;


        }

        protected void onPreExecute() {
            dialog = ProgressDialog.show(getActivity(),"zizoo","Loading...",true);
        }


        @Override
        protected ArrayList<String> doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }
            intent5 = new Intent(getActivity(),Trailers.class);
            intent5.putExtra("idi",params[1]);

            //         Log.v(LOG_TAG,params[1]);

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            String appid = "5945d52b08ed894690d920432fa4f0f0";

            try {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/"+params[1]+"?";
//                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";
                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
//                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, appid)
                        .build();
                URL url = new URL(builtUri.toString());

                //  Log.v("order", params[0]);
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won'jsst affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                JsonStr = buffer.toString();
                Log.v(LOG_TAG, " string: " + JsonStr);
            } catch (Exception e) {
                Log.e(LOG_TAG, "errorrr", e);
            } finally {
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
                return GetKeys(JsonStr,params[2]);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            super.onPostExecute(strings);
            String[] size= strings.toArray(new String[strings.size()]);
//                String[] size = (String[]) strings.toArray();
//                Log.v("reviews",size[0]);
            intent5.putExtra("keys",size);
            startActivity(intent5);
            dialog.cancel();
        }
    }
}
