package com.example.dbm0204.caltracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.net.ConnectException;

/**
 * Created by dbm0204 on 7/20/17.
 */

public class NetworkUtils {
    public static boolean isConnectedtoInternet(Context context){
        ConnectivityManager cm =(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork!= null && activeNetwork.isConnected();
    }

}
