package com.vibes.androidsdkexampleapp.controllers;

import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

/**
 * Created by marius.pop on 10/11/17.
 */

public interface ExampleControllerInterface {
    void registerDevice(VibesListener<Credential> listener);
    void unregisterDevice(VibesListener<Void> listener);
    void registerPush(VibesListener<Void> listener, String firebasePushToken);
    void unregisterPush(VibesListener<Void> listener);
}
