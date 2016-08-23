package com.cafelivro.mam.workorder;

import android.os.Bundle;

import com.cafelivro.mam.BasicActivity;
import com.cafelivro.mam.R;

/**
 * Created by baeks on 8/22/2016.
 */

public class WorkorderActivity extends BasicActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addView(R.id.content_basic,R.layout.content_wo);

    }
}
