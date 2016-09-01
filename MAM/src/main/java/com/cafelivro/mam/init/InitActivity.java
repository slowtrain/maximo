package com.cafelivro.mam.init;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.cafelivro.mam.R;
import com.cafelivro.mam.asset.AssetListActivity;

public class InitActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);


        getSupportActionBar().hide();
        InitAsyncTask asyncTask = new InitAsyncTask();
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }




    class InitAsyncTask extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            Intent intent = new Intent(getApplicationContext(), AssetListActivity.class);

            try {
                Thread.sleep(5000);
            }catch(Exception e){
                e.printStackTrace();
            }




            startActivity(intent);
            finish();
            return null;
        }
    }



}
