package com.vibes.androidsdkexampleapp.api;

import android.util.Log;

import com.vibes.vibes.Credential;
import com.vibes.vibes.Vibes;
import com.vibes.vibes.VibesListener;

/**
 * Created by jean-michel.barbieri on 2/25/18
 * Copyright (c) Vibes 2018 . All rights reserved.
 * Last modified 12:33 AM
 */
public class VibesAPI implements VibesAPIContract {
    @Override
    public void registerDevice(VibesListener<Credential> listener) {
        Vibes.getInstance().registerDevice(listener);
    }

    @Override
    public void unregisterDevice(VibesListener<Void> listener) {
        Vibes.getInstance().unregisterDevice(listener);
    }

    @Override
    public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
        Log.d("Controller", "--> Controller Firebase Token: " + firebasePushToken);
        Vibes.getInstance().registerPush(firebasePushToken, listener);
    }

    @Override
    public void unregisterPush(VibesListener<Void> listener) {
        Vibes.getInstance().unregisterPush(listener);
    }
}
