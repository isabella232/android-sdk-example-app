package com.vibes.androidsdkexampleapp.modelViews;

import android.util.Log;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.controllers.ExampleControllerInterface;
import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by marius.pop on 10/5/17.
 */

public class ExampleViewModel {

    private static final String TAG = "ExampleViewModel";

    private ExampleControllerInterface controller;
    private String token = "";
    private boolean isRegistered = false;


    /* Private subjects */
    private PublishSubject<String> observedByDevId = PublishSubject.create();
    private PublishSubject<String> observedByAuthToken = PublishSubject.create();
    private PublishSubject<Boolean> observeDisplayLoadingBar = PublishSubject.create();
    private PublishSubject<Integer> observedByDeviceRegBtn = PublishSubject.create();
    private PublishSubject<Boolean> observedByPushRegBtnEnabled = PublishSubject.create();
    private PublishSubject<Integer> observedByPushRegBtnBgColor = PublishSubject.create();
    private PublishSubject<Integer> observedByPushRegBtn = PublishSubject.create();
    private PublishSubject<Integer> observedByPushRegLabelBgColor = PublishSubject.create();
    private PublishSubject<Integer> observedByPushRegLabel = PublishSubject.create();

    /* Public observables */
    public Observable<String> devIdObservable = observedByDevId.share();
    public Observable<String> authTokenObservable = observedByAuthToken.share();
    public Observable<Boolean> displayLoadingBarObservable = observeDisplayLoadingBar.share();
    public Observable<Integer> deviceRegBtnObservable = observedByDeviceRegBtn.share();
    public Observable<Boolean> pushRegBtnEnabledObservable = observedByPushRegBtnEnabled.share();
    public Observable<Integer> pushRegBtnBgColorObservable = observedByPushRegBtnBgColor.share();
    public Observable<Integer> pushRegBtnObservable = observedByPushRegBtn.share();
    public Observable<Integer> pushRegLabelBgColorObservable = observedByPushRegLabelBgColor.share();
    public Observable<Integer> pushRegLabelObservable = observedByPushRegLabel.share();

    private ReplaySubject<String> firebaseTokenReplaySubject;

    public ExampleViewModel(Observable<Object> devRegObservable,
                            Observable<Object> pushRegObservable,
                            ReplaySubject<String> firebaseTokenReplaySubject,
                            ExampleControllerInterface controller) {
        this.controller = controller;
        this.firebaseTokenReplaySubject = firebaseTokenReplaySubject;
        setupDevRegObservable(devRegObservable);
        setupPushRegObservable(pushRegObservable);
    }

    /**
     * When a click event is triggered to register or unregister a device, we subscribe to the
     * Button.Rx.click observable that is passed in the constructor. The subscribe function calls
     * the controller to either register or unregister a device.
     */

    private void setupDevRegObservable(Observable<Object> devRegObservable) {
        devRegObservable
                .doOnNext(aVoid -> observeDisplayLoadingBar.onNext(true))
                .subscribe(aVoid -> {
                    if (token.length() == 0) {
                        controller.registerDevice(registerDeviceCallback());
                    } else {
                        controller.unregisterDevice(unregisterDeviceCallback());
                    }
                });
    }

    /**
     * When a click event is triggered to register or unregister push, we subscribe to the
     * Button.Rx.click that is passed in the constructor. The subscribe function calls
     * the controller to either register or unregister from push.
     */

    private void setupPushRegObservable(Observable<Object> pushRegObservable) {
        pushRegObservable
                .doOnNext(aVoid -> observeDisplayLoadingBar.onNext(true))
                .flatMap(aVoid -> firebaseTokenReplaySubject)
                .subscribe(firebasePushToken -> {
                    if (isRegistered) {
                        controller.unregisterPush(unregisterPushCallback());
                    } else {
                        controller.registerPush(registerPushCallback(), firebasePushToken);
                        Log.d(TAG, "--> " + firebasePushToken);
                    }
                });
    }

    /**
     * Callback to register a device with Vibes. This gets passed to the controller to be called
     * upon success or failure. The SDK stores the credentials locally so if multiple calls to
     * register device get triggered, the credentials are grabbed from the local storage. Upon
     * success the device id and auth token are sent back as part of the credential object.
     */

    private VibesListener<Credential> registerDeviceCallback() {
        return new VibesListener<Credential>() {
                @Override
                public void onSuccess(Credential credential) {
                    Log.d(TAG, "--> Register Device OK --> Token:" + credential.getAuthToken());
                    token = credential.getAuthToken();

                    observedByDevId.onNext(credential.getDeviceID());
                    observedByAuthToken.onNext(credential.getAuthToken());
                    observedByDeviceRegBtn.onNext(R.string.btn_unregister_device);
                    observedByPushRegBtnEnabled.onNext(true);
                    observedByPushRegBtnBgColor.onNext(R.color.vibesButtonColor);
                    observeDisplayLoadingBar.onNext(false);
                }

                @Override
                public void onFailure(String error) {
                    // handle on error
                    observeDisplayLoadingBar.onNext(false);
                    Log.d(TAG, "--> Register Device --> Error: " + error);
                }
            };
    }

    /**
     * Callback to unregister a device with Vibes. The callback gets passed to the controller to be
     * called upon success or failure. If the device was not unregistered from push notifications
     * prior to the unregister device request, the device will also be unregistered from push
     * notifications. When the unregister device is successful, the local credentials stored during the
     * device registration are deleted.
     */

    private VibesListener<Void> unregisterDeviceCallback() {
        return new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                token = "";
                isRegistered = false;

                observedByDevId.onNext("[Not Registered]");
                observedByAuthToken.onNext("[Not Registered]");
                observedByDeviceRegBtn.onNext(R.string.btn_register_device);
                observedByPushRegBtnEnabled.onNext(false);
                observedByPushRegBtnBgColor.onNext(R.color.vibesDisabledButtonColor);
                observedByPushRegBtn.onNext(R.string.btn_register_push);
                observedByPushRegLabelBgColor.onNext(R.color.red);
                observedByPushRegLabel.onNext(R.string.not_registered);
                observeDisplayLoadingBar.onNext(false);
                Log.d(TAG, "--> Unregister Device --> OK");
            }

            @Override
            public void onFailure(String error) {
                // handle error
                observeDisplayLoadingBar.onNext(false);
                Log.d(TAG, "--> Unregister Device --> Error: " + error);
            }
        };
    }

    /**
     * Callback to register push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device is ready to receive
     * push notifications.
     */

    private VibesListener<Void> registerPushCallback() {
        return new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                isRegistered = true;
                observedByPushRegBtn.onNext(R.string.btn_unregister_push);
                observedByPushRegLabelBgColor.onNext(R.color.green);
                observedByPushRegLabel.onNext(R.string.registered);
                observeDisplayLoadingBar.onNext(false);
                Log.d(TAG, "--> Registered push.");
            }

            @Override
            public void onFailure(String errorText) {
                // handle error
                observeDisplayLoadingBar.onNext(false);
                Log.d(TAG, errorText);
            }
        };
    }

    /**
     * Callback to unregister push notifications with Vibes. The callback gets passed to the
     * controller to be called upon success or failure. Upon success, the device will no longer
     * receive push notifications.
     */

    private VibesListener<Void> unregisterPushCallback() {
        return new VibesListener<Void>() {
            @Override
            public void onSuccess(Void value) {
                isRegistered = false;
                observedByPushRegBtn.onNext(R.string.btn_register_push);
                observedByPushRegLabelBgColor.onNext(R.color.red);
                observedByPushRegLabel.onNext(R.string.not_registered);
                observeDisplayLoadingBar.onNext(false);
                Log.d(TAG, "--> Unregistered push.");
            }

            @Override
            public void onFailure(String errorText) {
                // handle error
                observeDisplayLoadingBar.onNext(false);
                Log.d(TAG, errorText);
            }
        };
    }
}
