package com.example.muhammed.movie;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Trailers extends AppCompatActivity {

    ListView listView;
    ArrayList<String> keys;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trailers);
        final Intent intent = getIntent();
      final  String[] size= intent.getStringArrayExtra("keys");
//        for(int i =0 ;i < size.length;i++){
//            Log.v("keys",size[i]);
//        }
        String identity = intent.getStringExtra("idi").toString();
//
        ArrayList<String> trailers = new ArrayList<String>();
        trailers.clear();
        if(size.length !=0){
        for(int i=1; i<size.length+1;i++){

            if(identity.equals("videos")){
                trailers.add("Trailer"+i);
            }else if(identity.equals("reviews")){
                trailers.add(size[i-1]);
            }

        }
        }else{
            trailers.add("No reviews for that movie");
        }
        String[] keys= trailers.toArray(new String[trailers.size()]);

        ArrayAdapter adapter = new ArrayAdapter<String>(this,R.layout.list_view_item,keys);
        listView = (ListView) findViewById(R.id.listview);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Toast.makeText(getApplicationContext(),size[position],Toast.LENGTH_LONG).show();
//                Log.d("tag_setonItemClick","in onItemClick");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v="+size[position])));
            }
        });

    }
}
