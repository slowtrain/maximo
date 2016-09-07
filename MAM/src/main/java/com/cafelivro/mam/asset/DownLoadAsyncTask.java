package com.cafelivro.mam.asset;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.cafelivro.mam.R;
import com.cafelivro.mam.workorder.WorkorderListActivity;
import com.cafelivro.oslc.MaximoConnector;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Created by baeks on 9/4/2016.
 */

 class DownLoadAsyncTask extends AsyncTask<Void,String,Void>{
    private AssetListActivity activity;
    private ProgressBar progressBar;
    public DownLoadAsyncTask(Context context) {
        this.activity=(AssetListActivity)context;
        this.progressBar=(ProgressBar)activity.findViewById(R.id.progressBar);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Toast.makeText(activity,"DOWNLOAD 를 시작합니다.",Toast.LENGTH_SHORT).show();
        progressBar.setVisibility(View.VISIBLE);

    }

    @Override
    protected Void doInBackground(Void... voids) {

        SQLiteDatabase database=activity.openOrCreateDatabase(activity.getString(R.string.database_name), Context.MODE_PRIVATE,null);



        String oslcWhere   = "oslc.where=spi:siteid=\"BEDFORD\"";
        String oslcSelect  = "oslc.select=spi:assetnum,spi:siteid,spi:description,spi:location";

        MaximoConnector connector = new MaximoConnector(activity);
        JSONObject result=connector.query("oslcasset",oslcWhere,oslcSelect);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");

        database.execSQL("delete from asset");
        ContentValues values;
        for(int i=0;i<assetSet.size();i++){

            JSONObject asset=(JSONObject)assetSet.get(i);
            String resourceIdentifier=(String)asset.get("rdf:about");

            String assetnum=(String)asset.get("spi:assetnum");
            String siteid=(String)asset.get("spi:siteid");
            String description=(String)asset.get("spi:description");
            String location=(String)asset.get("spi:location");
            System.out.println(resourceIdentifier);
            System.out.println(assetnum +" // "+description+" // "+siteid);

            values=new ContentValues();

            values.put("assetnum",assetnum);
            values.put("siteid",siteid);
            values.put("description",description);
            values.put("location",location);


            database.insert("ASSET",null,values);


        }

        System.out.println("patch count : "+assetSet.size());


        Cursor cursor=database.rawQuery("select count(1) cnt from ASSET",null);
        if(cursor!=null){
            cursor.moveToNext();
            Log.d("dbcount",cursor.getInt(0)+"");
        }
        cursor.close();
        database.close();

        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressBar.setVisibility(View.INVISIBLE);
        activity.adapter.setDataSet();
        activity.adapter.notifyDataSetChanged();

        Toast.makeText(activity,"DOWNLOAD 가 완료 되었습니다.",Toast.LENGTH_SHORT).show();

    }


}
