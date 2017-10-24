package com.vibes.androidsdkexampleapp.controllers;

import android.util.Log;

import com.vibes.vibes.Credential;
import com.vibes.vibes.Vibes;
import com.vibes.vibes.VibesListener;

/**
 * Created by marius.pop on 10/6/17.
 */

public class ExampleController implements ExampleControllerInterface {
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
