package com.cafelivro.mam.asset;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.cafelivro.mam.BasicActivity;
import com.cafelivro.mam.R;

/**
 * Created by baeks on 8/22/2016.
 */

public class AssetActivity extends BasicActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addView(R.id.content_basic,R.layout.content_asset);

        Button button = (Button)findViewById(R.id.asset_move_list);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AssetActivity.this,AssetListActivity.class);

                startActivity(intent);
            }
        });


    }
}
