package com.vibes.androidsdkexampleapp.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by marius.pop on 10/12/17.
 */

public class FirebaseIDService extends FirebaseInstanceIdService {

    public final static String FIREBASE_TOKEN = "firebase_token";

    private final static String TAG = "FirebaseIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Intent intent = new Intent(FIREBASE_TOKEN);
        intent.putExtra(FIREBASE_TOKEN, refreshedToken);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        Log.d(TAG, "--> Token Refreshed: " + refreshedToken);
    }
}
