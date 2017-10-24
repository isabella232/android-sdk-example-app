package com.vibes.androidsdkexampleapp.modelViewsTest;

import android.util.Log;

import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.controllers.ExampleControllerInterface;
import com.vibes.androidsdkexampleapp.modelViews.ExampleViewModel;
import com.vibes.vibes.Credential;
import com.vibes.vibes.VibesListener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.reactivex.observers.TestObserver;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by marius.pop on 10/11/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ExampleViewModelOnSuccessTest {

    private class DummyController implements ExampleControllerInterface {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
            listener.onSuccess(null);
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
            listener.onSuccess(null);
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
            listener.onSuccess(null);
        }
    }

    private ExampleViewModel viewModel;
    private PublishSubject<Object> registerDeviceClick = PublishSubject.create();
    private PublishSubject<Object> registerPushClick = PublishSubject.create();
    private ReplaySubject<String> firebaseTokenReplaySubject = ReplaySubject.create();
    private DummyController controller = new DummyController();

    @Before
    public void setup() {
        PowerMockito.mockStatic(Log.class);
        viewModel = new ExampleViewModel(registerDeviceClick, registerPushClick,
                firebaseTokenReplaySubject, controller);
    }

    @Test
    public void loadingBarGoesFromVisibleToGone() {
        TestObserver<Boolean> observer = new TestObserver<>();
        viewModel.displayLoadingBarObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValues(true, false);
    }

    @Test
    public void onRegDeviceWeGetCorrectDeviceId() {
        TestObserver<String> observer = new TestObserver<>();
        viewModel.devIdObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValue("device_id");
    }

    @Test
    public void onRegDeviceWeGetCorrectToken() {
        TestObserver<String> observer = new TestObserver<>();
        viewModel.authTokenObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValue("auth_token");
    }

    @Test
    public void onRegDeviceWeSetRegBtnTextToUnregisterDevice() {
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.deviceRegBtnObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValue(R.string.btn_unregister_device);
    }

    @Test
    public void onRegDeviceWeEnablePushRegBtn() {
        TestObserver<Boolean> observer = new TestObserver<>();
        viewModel.pushRegBtnEnabledObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValue(true);
    }

    @Test
    public void onRegDeviceWeWeSetVibesBtnColorToVibesButtonCollor() {
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.pushRegBtnBgColorObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValue(R.color.vibesButtonColor);
    }

    @Test
    public void onUnregDeviceWeSetDevIdToNotRegistered() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<String> observer = new TestObserver<>();
        viewModel.devIdObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue("[Not Registered]");
    }

    @Test
    public void onUnregDeviceWeSetAuthTokenToNotRegistered() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<String> observer = new TestObserver<>();
        viewModel.authTokenObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue("[Not Registered]");
    }

    @Test
    public void onUnregDeviceWeSetRegBtnTextToRegisterDevice() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.deviceRegBtnObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue(R.string.btn_register_device);
    }

    @Test
    public void onUnregDeviceWeDisablePushRegBtn() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Boolean> observer = new TestObserver<>();
        viewModel.pushRegBtnEnabledObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue(false);
    }

    @Test
    public void onUnregDeviceWeWeSetVibesBtnColorToDisabledVibesButtonCollor() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.pushRegBtnBgColorObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue(R.color.vibesDisabledButtonColor);
    }

    @Test
    public void onUnregDeviceWeWeSetPushRegBtnTextToRegisterPush() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.pushRegBtnObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue(R.string.btn_register_push);
    }

    @Test
    public void onUnregDeviceWeWeSetPushRegLabelColorToRed() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.pushRegLabelBgColorObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue(R.color.red);
    }

    @Test
    public void onUnregDeviceWeWeSetPushRegLabelTextToNotRegistered() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        viewModel.pushRegLabelObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Unregister Device Click");
        observer.assertValue(R.string.not_registered);
    }

    @Test

    public void onPushRegPushRegBtnSetTextToUnregisterPush() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        firebaseTokenReplaySubject.onNext("firebase_token");
        viewModel.pushRegBtnObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Register Push Click");
        observer.assertValue(R.string.btn_unregister_push);
    }

    @Test
    public void onPushRegPushRegBtnSetPushLabelColorToGreen() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        firebaseTokenReplaySubject.onNext("firebase_token");
        viewModel.pushRegLabelBgColorObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Register Push Click");
        observer.assertValue(R.color.green);
    }

    @Test
    public void onPushRegPushRegBtnSetPushLabelTestToRegistered() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        firebaseTokenReplaySubject.onNext("firebase_token");
        viewModel.pushRegLabelObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Register Push Click");
        observer.assertValue(R.string.registered);
    }

    @Test
    public void onPushUnregPushRegBtnSetTextToRegisterPush() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        firebaseTokenReplaySubject.onNext("firebase_token");
        registerPushClick.onNext("Simulate Register Push Click");
        viewModel.pushRegBtnObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Unregister Push Click");
        observer.assertValue(R.string.btn_register_push);
    }

    @Test
    public void onPushUnregPushRegBtnSetPushLabelColorToRed() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        firebaseTokenReplaySubject.onNext("firebase_token");
        TestObserver<Integer> observer = new TestObserver<>();
        registerPushClick.onNext("Simulate Register Push Click");
        viewModel.pushRegLabelBgColorObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Unregister Push Click");
        observer.assertValue(R.color.red);
    }

    @Test
    public void onPushUnregPushRegBtnSetPushLabelTestToUnregistered() {
        registerDeviceClick.onNext("Simulate Register Device Click");
        TestObserver<Integer> observer = new TestObserver<>();
        firebaseTokenReplaySubject.onNext("firebase_token");
        registerPushClick.onNext("Simulate Register Push Click");
        viewModel.pushRegLabelObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Unregister Push Click");
        observer.assertValue(R.string.not_registered);
    }
}
