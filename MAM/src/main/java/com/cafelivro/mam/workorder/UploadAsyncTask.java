package com.cafelivro.mam.workorder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Created by baeks on 9/4/2016.
 */

 class UploadAsyncTask extends AsyncTask<Void,String,Void>{
    private Context context;
    public UploadAsyncTask(Context context) {
        this.context=context;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        SQLiteDatabase database=context.openOrCreateDatabase("eam.db", Context.MODE_PRIVATE,null);

        Log.d("db path ",context.getDatabasePath("eam.db").getPath());

        StringBuilder sql=new StringBuilder();
        sql.append(" SELECT ");
        sql.append(" * ");
        sql.append(" FROM ");
        sql.append(" WORKORDER ");

        Cursor cursor= database.rawQuery(sql.toString(),null);
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        jsonObject.put("workorder",jsonArray);
        if(cursor!=null){

            while(cursor.moveToNext()){

            }
        }
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
