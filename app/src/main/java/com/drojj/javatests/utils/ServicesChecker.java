package com.drojj.javatests.utils;

import android.content.Context;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

public class ServicesChecker {

    public static boolean isGooglePlayServicesAvailable(Context context) {
        GoogleApiAvailability api = GoogleApiAvailability.getInstance();
        return api.isGooglePlayServicesAvailable(context) == ConnectionResult.SUCCESS;
    }
}
