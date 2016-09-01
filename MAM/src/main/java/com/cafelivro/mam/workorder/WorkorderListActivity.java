package com.cafelivro.mam.workorder;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cafelivro.mam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkorderListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workorder_list);

        setUp();

        List<Map<String,Object>> assetSet = new ArrayList<Map<String,Object>>();

        for(int i =1;i<101;i++){
            Map<String,Object> asset = new HashMap<String,Object>();
            asset.put("assetnum","test00"+i);
            asset.put("description","desc00"+i);
            asset.put("location","location00"+i);
            asset.put("siteid","CAFE");
            assetSet.add(asset);
        }

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.content_workorder_list);
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this,assetSet));

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
        getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_asset) {
            // Handle the camera action
        } else if (id == R.id.nav_workorder) {
            
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public class SimpleItemRecyclerViewAdapter extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {


        List<Map<String, Object>> assetSet;
        Map<String, Object> currentAsset;
        Context context;


        public SimpleItemRecyclerViewAdapter(Context context, List<Map<String, Object>> assetSet) {
            this.assetSet = assetSet;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_workorder, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            currentAsset = assetSet.get(position);

            holder.assetnum.setText((String) currentAsset.get("assetnum"));
            holder.description.setText((String) currentAsset.get("description"));
            holder.location.setText((String) currentAsset.get("location"));
            holder.siteid.setText((String) currentAsset.get("siteid"));

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();

                    Intent intent = new Intent(context, WorkorderActivity.class);
                    intent.putExtra("assetnum", (String) currentAsset.get("assetnum"));
                    intent.putExtra("description", (String) currentAsset.get("description"));
                    intent.putExtra("location", (String) currentAsset.get("location"));
                    intent.putExtra("siteid", (String) currentAsset.get("siteid"));
                    context.startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return assetSet.size();
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
