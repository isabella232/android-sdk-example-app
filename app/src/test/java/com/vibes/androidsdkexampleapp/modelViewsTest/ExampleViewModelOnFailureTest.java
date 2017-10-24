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
 * Created by marius.pop on 10/16/17.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({Log.class})
public class ExampleViewModelOnFailureTest {

    private ExampleViewModel viewModel;
    private PublishSubject<Object> registerDeviceClick = PublishSubject.create();
    private PublishSubject<Object> registerPushClick = PublishSubject.create();
    private ReplaySubject<String> firebaseTokenReplaySubject = ReplaySubject.create();
    private DummyControllerRegisterFails controllerRegisterFails =
            new DummyControllerRegisterFails();
    private DummyControllerUnregisterFails controllerUnregisterFails =
            new DummyControllerUnregisterFails();
    private DummyControllerRegisterPushFails controllerRegisterPushFails =
            new DummyControllerRegisterPushFails();
    private DummyControllerUnregisterPushFails controllerUnregisterPushFails =
            new DummyControllerUnregisterPushFails();

    @Before
    public void setup() {
        PowerMockito.mockStatic(Log.class);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnRegisterFails() {
        viewModel = new ExampleViewModel(registerDeviceClick, registerPushClick,
                firebaseTokenReplaySubject, controllerRegisterFails);
        TestObserver<Boolean> observer = new TestObserver<>();
        viewModel.displayLoadingBarObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValues(true, false);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnUnregisterFails() {
        viewModel = new ExampleViewModel(registerDeviceClick, registerPushClick,
                firebaseTokenReplaySubject, controllerUnregisterFails);
        TestObserver<Boolean> observer = new TestObserver<>();
        registerDeviceClick.onNext("Simulate Register Device Click");
        viewModel.displayLoadingBarObservable.subscribe(observer);
        registerDeviceClick.onNext("Simulate Register Device Click");
        observer.assertValues(true, false);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnRegisterPushFails() {
        viewModel = new ExampleViewModel(registerDeviceClick, registerPushClick,
                firebaseTokenReplaySubject, controllerRegisterPushFails);
        TestObserver<Boolean> observer = new TestObserver<>();
        registerDeviceClick.onNext("Simulate Register Device Click");
        viewModel.displayLoadingBarObservable.subscribe(observer);
        firebaseTokenReplaySubject.onNext("firebase_token");
        registerPushClick.onNext("Simulate Register Push Click");
        observer.assertValues(true, false);
    }

    @Test
    public void loadingBarGoesFromVisibleToGoneOnUnregisterPushFails() {
        viewModel = new ExampleViewModel(registerDeviceClick, registerPushClick,
                firebaseTokenReplaySubject, controllerUnregisterPushFails);
        TestObserver<Boolean> observer = new TestObserver<>();
        registerDeviceClick.onNext("Simulate Register Device Click");
        firebaseTokenReplaySubject.onNext("firebase_token");
        registerPushClick.onNext("Simulate Register Push Click");
        viewModel.displayLoadingBarObservable.subscribe(observer);
        registerPushClick.onNext("Simulate Register Push Click");
        observer.assertValues(true, false);
    }

    private class DummyControllerRegisterFails implements ExampleControllerInterface {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            listener.onFailure("Failure message");
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
        }
    }

    private class DummyControllerUnregisterFails implements ExampleControllerInterface {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
            listener.onFailure("Failure message");
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
        }
    }

    private class DummyControllerRegisterPushFails implements ExampleControllerInterface {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
            listener.onFailure("Failure message");
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
        }
    }

    private class DummyControllerUnregisterPushFails implements ExampleControllerInterface {

        @Override
        public void registerDevice(VibesListener<Credential> listener) {
            Credential credential = new Credential("device_id", "auth_token");
            listener.onSuccess(credential);
        }

        @Override
        public void unregisterDevice(VibesListener<Void> listener) {
        }

        @Override
        public void registerPush(VibesListener<Void> listener, String firebasePushToken) {
            listener.onSuccess(null);
        }

        @Override
        public void unregisterPush(VibesListener<Void> listener) {
            listener.onFailure("Failure message");
        }
    }
}
