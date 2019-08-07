package com.example.final_project;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity {
    ImageView iv1 = null;
    Button b1 = null;
    Button b2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        iv1 = (ImageView)findViewById(R.id.iv1);
        b1 = (Button)findViewById(R.id.b1);
        b2 = (Button)findViewById(R.id.b2);

        //for the image -- also make sure that you use https not http
        try {
            new MyTask().execute("https://static.tvmaze.com/uploads/images/original_untouched/4/11403.jpg").get();
        }catch(Exception e){
            Log.i("AppError",e.getMessage());
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Lit show :]", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent seasons = new Intent(v.getContext(), Main2Activity.class);
                startActivity(seasons);
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent life_cycle = new Intent(v.getContext(), Life_Cycle.class);
                startActivity(life_cycle);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    //this is the little drop down menu (3 vertical dots)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //This is to shut down the app from the menu
        if (id == R.id.action_settings) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            //return true;
//            this.finish();
            builder.setMessage("Do you want to close this App?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            //setting the builder to alert to be able to get a title up
            AlertDialog alert = builder.create();
            alert.setTitle("YO YOU GOT A MESSAGE!");
            alert.show();
        }

        return super.onOptionsItemSelected(item);
    }

    private class MyTask extends AsyncTask<String, Void, Bitmap> {

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap myIcon = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                myIcon = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("AppError", e.getMessage());
                e.printStackTrace();
            }
            return myIcon;
        }
        protected void onPostExecute(Bitmap result) {
            iv1.setImageBitmap(result);
        }
    }
}
