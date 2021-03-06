package com.cafelivro.mam.workorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cafelivro.mam.R;
import com.cafelivro.mam.util.Utils;

public class WorkorderActivity extends AppCompatActivity {

    public static final String PATH = "path";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder);
        setUp();
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


        Intent intent=getIntent();
        String wonum=intent.getStringExtra("wonum");
        String siteid=intent.getStringExtra("siteid");
        String assetnum=intent.getStringExtra("assetnum");
        String description=intent.getStringExtra("description");


        TextView tvAssetnum=(TextView)findViewById(R.id.assetnum);
        TextView tvDescription=(TextView)findViewById(R.id.description);
        TextView tvWonum=(TextView)findViewById(R.id.wonum);
        TextView tvSiteid=(TextView)findViewById(R.id.siteid);
        tvAssetnum.setText(assetnum);
        tvDescription.setText(description);
        tvWonum.setText(wonum);
        tvSiteid.setText(siteid);



        //animation effect
        RelativeLayout view = (RelativeLayout) findViewById(R.id.content_workorder);
        ViewCompat.setTransitionName(view, Utils.TRANSITION_NAME);



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(0,0);
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
            navigateUpTo(new Intent(this, WorkorderListActivity.class));
            finish();
            overridePendingTransition(0,0);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
