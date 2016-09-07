package com.cafelivro.mam.workorder;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.cafelivro.mam.R;
import com.cafelivro.oslc.MaximoConnector;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Created by baeks on 9/4/2016.
 */

 class RenderListAsyncTask extends AsyncTask<Void,String,Void>{
    private Activity activity;
    public RenderListAsyncTask(Context context) {
        this.activity=(Activity)context;
    }

    @Override
    protected Void doInBackground(Void... voids) {


        SQLiteDatabase database=activity.openOrCreateDatabase(activity.getString(R.string.database_name), Context.MODE_PRIVATE,null);

        String oslcWhere   = "oslc.where=spi:siteid=\"BEDFORD\"";
        String oslcSelect  = "oslc.select=spi:wonum,spi:siteid,spi:description,spi:siteid,spi:assetnum";

        MaximoConnector connector = new MaximoConnector(activity);
        JSONObject result=connector.query("oslcwodetail",oslcWhere,oslcSelect);
        JSONArray assetSet =(JSONArray)result.get("rdfs:member");
        ContentValues values;
        for(int i=0;i<assetSet.size();i++){
            values=new ContentValues();


            JSONObject asset=(JSONObject)assetSet.get(i);
            String resourceIdentifier=(String)asset.get("rdf:about");
            String wonum=(String)asset.get("spi:wonum");
            String assetnum=(String)asset.get("spi:assetnum");
            String siteid=(String)asset.get("spi:siteid");
            String description=(String)asset.get("spi:description");
            System.out.println(resourceIdentifier);
            System.out.println(wonum +" // "+description+" // "+siteid);


            values.put("wonum",wonum);
            values.put("assetnum",assetnum);
            values.put("siteid",siteid);
            values.put("description",description);


            database.insert("WORKORDER",null,values);
//            Iterator it= asset.keySet().iterator();
//            while(it.hasNext()){
//                Object key = it.next();
//                System.out.println(key.getClass().getSimpleName());
//                System.out.println(key);
//            }
//
//            	    asset.put("spi:description", "1026 update");
//            	    asset.put("spi:changeby", "MAXADMIN");
//
//            	    toMaximo.update(resourceIdentifier, asset);

        }

        System.out.println("patch count : "+assetSet.size());



//        StringBuilder sql=new StringBuilder();
//        sql.append("INSERT INTO WORKORDER (");
//        sql.append("wonum,siteid,assetnum,location,siteid");
//        sql.append(") values (");
//        sql.append("             ");
//        sql.append(")");
//        database.insert("WORKORDER",null,)
//        database.execSQL(sql.toString());



        return null;
    }


    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);

    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);

    }


}
