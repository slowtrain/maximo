package com.cafelivro.mam.util;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by baeks on 9/7/2016.
 */

public class CommonValidator {

    public static Boolean wifiOn(Activity activity){
        Boolean wifiOn=false;
        ConnectivityManager cm=(ConnectivityManager)activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork=cm.getActiveNetworkInfo();
        Boolean isConnected=activeNetwork!=null&&activeNetwork.isConnectedOrConnecting();
        if(isConnected){
            if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI){
                return true;
            }
        }

        return wifiOn;
    }
}
