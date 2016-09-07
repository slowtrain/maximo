package com.cafelivro.mam.init;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.cafelivro.mam.R;
import com.cafelivro.mam.asset.AssetListActivity;
import com.cafelivro.mam.location.LocationListActivity;
import com.cafelivro.mam.setting.SettingActivity;
import com.cafelivro.mam.workorder.WorkorderListActivity;

/**
 * Created by baeks on 9/5/2016.
 */

    class InitAsyncTask extends AsyncTask<Void,String,Void> {

        private Activity activity;
        private TextView progressTxt;

        public InitAsyncTask(Activity activity){
            this.activity=activity;
            progressTxt=(TextView)activity.findViewById(R.id.progressTxt);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {

                Thread.sleep(2000);

                SQLiteDatabase db=activity.openOrCreateDatabase(activity.getString(R.string.database_name), Context.MODE_PRIVATE,null);

                //db.beginTransaction();
                Log.d("db path ",activity.getDatabasePath("eam.db").getPath());

//                db.execSQL("DROP TABLE IF EXISTS person");
//                db.execSQL("DROP TABLE IF EXISTS asset");
//                db.execSQL("DROP TABLE IF EXISTS workorder");
//                db.execSQL("DROP TABLE IF EXISTS locations");
//                db.execSQL("DROP TABLE IF EXISTS location");

                StringBuffer personDdl = new StringBuffer();
                personDdl.append(" CREATE TABLE IF NOT EXISTS PERSON (");
                personDdl.append("  personuid integer primary key autoincrement");
                personDdl.append(", personid text");
                personDdl.append(", displayname text");
                personDdl.append(")");
                db.execSQL(personDdl.toString());
                publishProgress("Person Created");


                StringBuffer assetDdl = new StringBuffer();
                assetDdl.append(" create table if not exists asset (");
                assetDdl.append("  assetid integer primary key autoincrement");
                assetDdl.append(", assetnum text");
                assetDdl.append(", siteid text");
                assetDdl.append(", description text");
                assetDdl.append(", location text");
                assetDdl.append(");");
                db.execSQL(assetDdl.toString());
                publishProgress("Asset Created");

                StringBuffer woDml = new StringBuffer();
                woDml.append(" create table if not exists workorder (");
                woDml.append(" workorderid integer primary key autoincrement");
                woDml.append(", wonum text");
                woDml.append(", assetnum text");
                woDml.append(", description text");
                woDml.append(", siteid text");
                woDml.append(");");
                db.execSQL(woDml.toString());
                publishProgress("Workorder Created");


                StringBuffer locationDml = new StringBuffer();
                locationDml.append(" CREATE TABLE IF NOT EXISTS LOCATIONS (");
                locationDml.append("  locationid integer primary key autoincrement");
                locationDml.append(", location text");
                locationDml.append(", siteid text");
                locationDml.append(", description text");
                locationDml.append(")");
                db.execSQL(locationDml.toString());

                publishProgress("FINISH INIT");


                Intent intent = new Intent(activity, getStartActivity());
                activity.startActivity(intent);
                activity.finish();

            }catch(Exception e){
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            progressTxt.setText(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressTxt.setText("FINISH INIT");

        }




    private Class getStartActivity(){

        if(activity.getString(R.string.start_module).equals("workorder")){
            return WorkorderListActivity.class;
        }else if(activity.getString(R.string.start_module).equals("asset")){
            return AssetListActivity.class;
        }else if(activity.getString(R.string.start_module).equals("location")){
            return LocationListActivity.class;
        }else if(activity.getString(R.string.start_module).equals("setting")){
            return SettingActivity.class;
        }else {

            return AssetListActivity.class;
        }
    }


}
