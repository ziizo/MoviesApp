package com.example.muhammed.movie;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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

class PlaceHolderFragment extends Fragment {
    GridView mygrid;
    ProgressDialog dialog;
    private ImageAdapter imageAdapter;
    Callback  callback;

    public PlaceHolderFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.datamenu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean isOnline() throws IOException, InterruptedException {
        ConnectivityManager cm =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public void onResume() {
        super.onResume();
        try {
            if (isOnline()) {

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String order = prefs.getString(getString(R.string.pref_order_key), getString(R.string.default_key));
                if (!isTablet(getActivity()))
                    updateData();
                else
                    callback.onDataRecieved(movies.get(0));
            }
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void updateData() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String order = prefs.getString(getString(R.string.pref_order_key), getString(R.string.default_key));
        if (order.equals("favourites")){
            final DBConnection dbConnection = new DBConnection(getActivity());
            imageAdapter = new ImageAdapter(getActivity(), R.layout.item, dbConnection.getAllMovies());
            if (((ArrayAdapter) mygrid.getAdapter()) != null) {
                ((ArrayAdapter) mygrid.getAdapter()).clear();
            }

            mygrid.setAdapter(imageAdapter);
            mygrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getActivity(), DetailActivity.class);
                    intent.putExtra("OriginalTitle", dbConnection.getAllMovies().get(position).getOriginalTitle());
                    intent.putExtra("PosterPath", dbConnection.getAllMovies().get(position).getImgPath());
                    intent.putExtra("OverView", dbConnection.getAllMovies().get(position).getOverView());
                    intent.putExtra("Rate", dbConnection.getAllMovies().get(position).getRate());
                    intent.putExtra("Date", dbConnection.getAllMovies().get(position).getDate());
                    intent.putExtra("Id", dbConnection.getAllMovies().get(position).getId());
                    startActivity(intent);
                }
            });
        }

        else{
            FetchData fetchData = new FetchData();
            fetchData.execute(order);
    }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        try {
            if (!isOnline()) {
                Toast.makeText(getActivity(), "No Network Access ...!",
                        Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mygrid = (GridView) rootView.findViewById(R.id.mygrid);

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        callback = (Callback) getActivity();

    }

    public class ImageAdapter extends ArrayAdapter {
        private Context context;
        private int resourse;
        private ArrayList<Movie> movieArray;

        public ImageAdapter(Context context, int resourse, ArrayList<Movie> strings) {//,ArrayList<Movie> movies
            super(context, resourse);
            this.resourse = resourse;
            this.context = context;
            this.movieArray = strings;
        }


        @Override
        public int getCount() {
            return movieArray.size();
        }//return legnth of gidview array

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View gridView;
            if (convertView == null) {
                gridView = new View(context);
                gridView = inflater.inflate(R.layout.item, null);

            } else {
                gridView = (View) convertView;
            }
            ImageView imageView = (ImageView) gridView
                    .findViewById(R.id.imageView);
            String url = "http://image.tmdb.org/t/p/w185/" + movieArray.get(position).getImgPath();
//            Log.v("url of image",url);
            Picasso.with(context).load(url).into(imageView);
            return gridView;

        }
    }

    ArrayList<Movie> movies = new ArrayList<>();

    public class FetchData extends AsyncTask<String, Void, ArrayList<Movie>> {
        private final String LOG_TAG = FetchData.class.getSimpleName();

        private ArrayList<Movie> getDataFromJson(String JsonStr)
                throws JSONException {
            movies.clear();
            final String OWM_ID = "id";
            final String OWM_RESULTS = "results";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_ORIGINAL_TITLE = "original_title";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_OVERVIEW = "overview";
            final String OWM_VOTE_AVERAGE = "vote_average";
            JSONObject JsonData = new JSONObject(JsonStr);
            JSONArray ResultsArray = JsonData.getJSONArray(OWM_RESULTS);
            for (int i = 0; i < ResultsArray.length(); i++) {
                Movie movie = new Movie();
                String poster_path;
                String Original_title;
                String Over_view;
                String date;
                String rate;
                String id;
                JSONObject object = ResultsArray.getJSONObject(i);

                id = object.getString(OWM_ID);
                poster_path = object.getString(OWM_POSTER_PATH);
                Original_title = object.getString(OWM_ORIGINAL_TITLE);
                Over_view = object.getString(OWM_OVERVIEW);
                date = object.getString(OWM_RELEASE_DATE);
                rate = object.getString(OWM_VOTE_AVERAGE);
                movie.setImgPath(poster_path);
                movie.setOriginalTitle(Original_title);
                movie.setOverView(Over_view);
                movie.setRate(rate);
                movie.setDate(date);
                movie.setId(id);
                movies.add(movie);

            }


            return movies;
        }
        protected void onPreExecute() {
             dialog = ProgressDialog.show(getActivity(),"zizoo","Loading...",true);
        }

        @Override
        protected ArrayList<Movie> doInBackground(String... params) {
            // Log.v(LOG_TAG,"d5al doinbackground");
            if (params.length == 0) {
                return null;
            }

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;
            String JsonStr = null;
            String appid = "5945d52b08ed894690d920432fa4f0f0";
            try {
                final String MOVIES_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "?";
//                final String SORT_PARAM = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(MOVIES_BASE_URL).buildUpon()
//                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(APPID_PARAM, appid)
                        .build();
                URL url = new URL(builtUri.toString());

              //  Log.v("order", params[0]);
                Log.v(LOG_TAG, "Built URI " + builtUri.toString());


//            URL url = new URL("http://api.themoviedb.org/3/discover/movie?sort_by=popularity.desc&api_key=5945d52b08ed894690d920432fa4f0f0");
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
// how to use picasso ?
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
                return getDataFromJson(JsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(final ArrayList<Movie> strings) {
            if (strings != null) {
                imageAdapter = new ImageAdapter(getActivity(), R.layout.item, strings);
                if (((ArrayAdapter) mygrid.getAdapter()) != null) {
                    ((ArrayAdapter) mygrid.getAdapter()).clear();
                }

                mygrid.setAdapter(imageAdapter);
                ;
                mygrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), DetailActivity.class);
                        intent.putExtra("OriginalTitle", strings.get(position).getOriginalTitle());
                        intent.putExtra("PosterPath", strings.get(position).getImgPath());
                        intent.putExtra("OverView", strings.get(position).getOverView());
                        intent.putExtra("Rate", strings.get(position).getRate());
                        intent.putExtra("Date", strings.get(position).getDate());
                        intent.putExtra("Id", strings.get(position).getId());
                        startActivity(intent);
                    }
                });
            }
            super.onPostExecute(strings);
            dialog.cancel();
        }
    }
}
