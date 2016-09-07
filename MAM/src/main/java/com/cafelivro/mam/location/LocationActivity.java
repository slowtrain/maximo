package com.cafelivro.mam.location;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.cafelivro.mam.R;
import com.cafelivro.mam.workorder.WorkorderListActivity;

public class LocationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        setUp();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Intent intent=getIntent();
        String location=intent.getStringExtra("location");
        Log.d("location",location);
        String siteid=intent.getStringExtra("siteid");
        String description=intent.getStringExtra("description");

        TextView tvLocation=(TextView)findViewById(R.id.location);
        TextView tvSiteid=(TextView)findViewById(R.id.siteid);
        TextView tvDescription=(TextView)findViewById(R.id.description);

        tvLocation.setText(location);
        tvSiteid.setText(siteid);
        tvDescription.setText(description);


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setUp(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            navigateUpTo(new Intent(this, LocationListActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
