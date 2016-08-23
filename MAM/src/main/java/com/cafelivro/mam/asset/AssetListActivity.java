package com.cafelivro.mam.asset;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cafelivro.mam.BasicActivity;
import com.cafelivro.mam.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by baeks on 8/22/2016.
 */

public class AssetListActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addView(R.id.content_basic,R.layout.content_asset_list);

        List<Map<String,Object>> assetSet = new ArrayList<Map<String,Object>>();

        for(int i =1;i<10;i++){
            Map<String,Object> asset = new HashMap<String,Object>();
            asset.put("wonum","test00"+i);
            asset.put("description","desc00"+i);
            asset.put("location","location00"+i);
            assetSet.add(asset);
        }

//        ArrayAdapter<String> adp= new ArrayAdapter<String>(this,android.R.layout.simple_expandable_list_item_1,asset);
//        ListView list = (ListView)findViewById(R.id.content_asset_list_list);
//        list.setAdapter(adp);
//        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
//        list.setDivider(new ColorDrawable(Color.DKGRAY));
//        list.setDividerHeight(2);


        ListView assetListView = (ListView)findViewById(R.id.content_asset_list_list);
        ArrayAdapter assetListadp = new AssetListAdapter(this,R.layout.list_asset,R.id.content_asset_list_list,assetSet);

        assetListView.setAdapter(assetListadp);

    }




    public class AssetListAdapter extends ArrayAdapter<Map<String,Object>> {

        Context context = null;
        int layoutResourceId = 0;
        int textViewResourceId = 0;
        List<Map<String,Object>> assetSet;

        public AssetListAdapter(Context context, int layoutResourceId, int textViewResourceId, List<Map<String,Object>> assetSet) {
            super(context, layoutResourceId, textViewResourceId, assetSet);

            this.context = context;
            this.layoutResourceId = layoutResourceId;
            this.textViewResourceId = textViewResourceId;
            this.assetSet = assetSet;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout layoutAssetList = null;

            if (null != convertView) {
                layoutAssetList = (LinearLayout) convertView;
            }else {
                LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layoutAssetList = (LinearLayout) layoutInflater.inflate(layoutResourceId, null);
            }
            TextView wonum = (TextView)layoutAssetList.findViewById(R.id.wonum);
            TextView description = (TextView)layoutAssetList.findViewById(R.id.description);
            TextView location = (TextView)layoutAssetList.findViewById(R.id.location);
            Map<String,Object> currentAsset = assetSet.get(position);

            wonum.setText((String)currentAsset.get("wonum"));
            description.setText((String)currentAsset.get("description"));
            location.setText((String)currentAsset.get("location"));

            return (View)layoutAssetList;
            }
    }



}
