package com.drojj.javatests.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ServicesChecker {

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        return api.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }

    public static boolean isInternetAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();

        return info != null && info.isConnectedOrConnecting();
    }
}
