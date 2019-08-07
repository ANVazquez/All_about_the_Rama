package com.example.final_project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// this screen is for the SEASONS button
// this will show the seasons in a spinner and once clicked it will have a bunch of shows that are in the season with the description of that ep
// along with an intent going to youtube or somewhere with the ep on it.
public class Main2Activity extends AppCompatActivity {
    //Class (global) variable declarations
    Spinner s1 = null;
    TextView et1 = null;
    ImageView iv1 = null;
    Button b1 = null;

    ArrayList<futurama> future = new ArrayList<futurama>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        s1 = (Spinner)findViewById(R.id.s1);
        et1 = (EditText)findViewById(R.id.et1);
        iv1 = (ImageView)findViewById(R.id.iv1);
        b1 = (Button)findViewById(R.id.b1);

        //starting the thread
        JSONThread jt = new JSONThread();
        jt.start();
        try {
            jt.join();
        } catch (InterruptedException ie) {
            Log.i("AppError", ie.getMessage());
        }


        // making an array list to add seasons
        final ArrayList<String> rama = new ArrayList<>();
        for (int x = 0; x < future.size(); ++x){
            //instead of using a hashset to save time i just pushed everything inside the spinner
            //besides the summary
            rama.add("Season " + future.get(x).season + " Episode " + future.get(x).episode);
        }
        // sets make it so there's no duplicating items
//        Set<String> setSeasons = new HashSet<String>(rama);

//        ArrayList<String> seasonList = new ArrayList<>();
////        for (int i = 0; i < setSeasons.size(); ++i){
////            seasonList.add(setSeasons.toArray()[i].toString());
////        }
        s1.setAdapter(new ArrayAdapter<String>(Main2Activity.this,
                android.R.layout.simple_spinner_dropdown_item, rama));
        s1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, final int position, long id) {
                Log.i("Season", future.get(position).season);
                et1.setText(future.get(position).summary);
                try {
                    new imgTask().execute(future.get(position).image).get();
                }catch(Exception e){
                    Log.i("AppError",e.getMessage());
                }
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent startup = new Intent();
                        startup.setAction(Intent.ACTION_VIEW);
                        startup.addCategory(Intent.CATEGORY_BROWSABLE);
                        startup.setData(Uri.parse(future.get(position).link));
                        startActivity(startup);
                    }
                });
//                ArrayList<String> epp = new ArrayList<>();
//                for (int a = 0; a < future.get(position).season.length(); ++a) {
//                    if(future.get(a).season ==future.get(position).season ){
//                        epp.add(future.get(a).episode);
//                    }
//                }
//                s2.setAdapter(new ArrayAdapter<String>(Main2Activity.this,
//                        android.R.layout.simple_spinner_dropdown_item,epp));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    class futurama {
        public String season;
        public String episode;
        public String summary;
        public String image;
        public String link;

        public futurama(String sea, String ep, String sum, String img, String ink) {
            this.season = sea;
            this.episode = ep;
            this.summary = sum;
            this.image = img;
            this.link = ink;
        }
    }

    private class JSONThread extends Thread {
        int ct; //counter variable
        StringBuilder wholefile = new StringBuilder(); // this reads a stream from web server

//        StringBuilder eps = new StringBuilder();
//        StringBuilder sums = new StringBuilder();

        String se = null;
        String ep = null;
        String sum = null;

        @Override
        public void run() {
            try {
                URL data = new URL("https://api.tvmaze.com/singlesearch/shows?q=Futurama&embed=episodes");
                InputStream is = data.openStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String temp = "";

                while ((temp = br.readLine()) != null) {
                    wholefile.append(temp);
                }
                br.close();
                String str = wholefile.toString();

                //grabbing the whole file because it's an object
                JSONObject objWhole = new JSONObject(wholefile.toString());
                //making it a string
                String ebmedded = objWhole.getString("_embedded");
                //grabbing the whole embedded object
                JSONObject objEmbedd = new JSONObject(ebmedded);
                //making embedded a string
                String episssssssssssodes = objEmbedd.getString("episodes");
                JSONArray jay = new JSONArray(episssssssssssodes);
                Log.i("tag", "Message Here");

                for (ct = 0; ct <= jay.length() - 1; ++ct) {
                    String season = jay.getJSONObject(ct).getString("season");
                    String episode = jay.getJSONObject(ct).getString("number");
                    String summary = jay.getJSONObject(ct).getString("summary");
                    String link = jay.getJSONObject(ct).getString("url");
                    JSONObject medimg = new JSONObject(jay.getJSONObject(ct).getString("image"));
                    String image = medimg.getString("medium");
                    Log.e("Sum", episode.toString());
                    Log.e("Sum", summary.toString());
                    Log.e("IMage", image);
                    futurama f = new futurama(season, episode, summary, image, link);
                    future.add(ct, f);
                }

            } catch (IOException ioe) {
                Log.i("AppError****", ioe.getMessage());
            } catch (Exception e) {
                Log.i("AppError----", e.getMessage());
            }
        }
    }

    private class imgTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap myIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                myIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("AppError++++", e.getMessage());
                e.printStackTrace();
            }
            return myIcon;
        }
        protected void onPostExecute(Bitmap result) {
            iv1.setImageBitmap(result);
        }
    }
}
