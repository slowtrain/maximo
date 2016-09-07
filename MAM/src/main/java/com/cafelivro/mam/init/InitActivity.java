package com.cafelivro.mam.init;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.cafelivro.mam.R;
import com.cafelivro.mam.asset.AssetListActivity;
import com.cafelivro.mam.location.LocationListActivity;
import com.cafelivro.mam.workorder.WorkorderListActivity;

public class InitActivity extends AppCompatActivity {

    private TextView progressTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);

        getSupportActionBar().hide();
        InitAsyncTask asyncTask = new InitAsyncTask(this);
        asyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

}
