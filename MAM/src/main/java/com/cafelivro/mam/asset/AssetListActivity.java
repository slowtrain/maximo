package com.cafelivro.mam.asset;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cafelivro.mam.R;
import com.cafelivro.mam.location.LocationListActivity;
import com.cafelivro.mam.setting.SettingActivity;
import com.cafelivro.mam.workorder.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AssetListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_list);

        setUp();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_asset_list);
        adapter = new SimpleItemRecyclerViewAdapter(this);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this));

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.asset_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_download) {
            DownLoadAsyncTask downloadAsyncTask = new DownLoadAsyncTask(this);
            downloadAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_workorder) {
            Intent intent = new Intent(this, WorkorderListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_asset) {

        } else if (id == R.id.nav_location) {
            Intent intent = new Intent(this, LocationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_setting) {
            Intent intent = new Intent(this, SettingActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        List<Map<String, Object>> dataSet;
        Context context;


        public SimpleItemRecyclerViewAdapter(Context context) {
            this.context = context;
            setDataSet();

        }

        public void setDataSet(){
            SQLiteDatabase database=context.openOrCreateDatabase(context.getString(R.string.database_name), Context.MODE_PRIVATE,null);
            Cursor cursor=database.rawQuery("select assetnum,siteid,description,location from asset", null);

            this.dataSet = new ArrayList<Map<String,Object>>();

            while(cursor.moveToNext()){
                Map<String,Object> asset = new HashMap<String,Object>();
                asset.put("assetnum",cursor.getString(0));
                asset.put("siteid",cursor.getString(1));
                asset.put("description",cursor.getString(2));
                asset.put("location",cursor.getString(3));

                dataSet.add(asset);
            }
        }



        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_asset, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            Map<String, Object> currentAsset= dataSet.get(position);

            holder.assetnum.setText((String) currentAsset.get("assetnum"));
            holder.description.setText((String) currentAsset.get("description"));
            holder.location.setText((String) currentAsset.get("location"));
            holder.siteid.setText((String) currentAsset.get("siteid"));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, AssetActivity.class);
                    intent.putExtra("assetnum", holder.assetnum.getText().toString());
                    intent.putExtra("description", holder.description.getText().toString());
                    intent.putExtra("location", holder.location.getText().toString());
                    intent.putExtra("siteid", holder.siteid.getText().toString());
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return dataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            public final View mView;
            public final TextView assetnum;
            public final TextView description;
            public final TextView location;
            public final TextView siteid;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                assetnum = (TextView) view.findViewById(R.id.assetnum);
                description = (TextView) view.findViewById(R.id.description);
                location = (TextView) view.findViewById(R.id.location);
                siteid = (TextView) view.findViewById(R.id.siteid);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + description.getText() + "'";
            }
        }
    }
}
