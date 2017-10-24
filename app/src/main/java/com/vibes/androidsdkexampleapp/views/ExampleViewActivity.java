package com.vibes.androidsdkexampleapp.views;

import android.content.BroadcastReceiver; import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;
import com.vibes.androidsdkexampleapp.R;
import com.vibes.androidsdkexampleapp.controllers.ExampleController;
import com.vibes.androidsdkexampleapp.modelViews.ExampleViewModel;
import com.vibes.androidsdkexampleapp.services.FirebaseIDService;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.ReplaySubject;

/**
 * Created by marius.pop on 10/10/17.
 */

public class ExampleViewActivity extends AppCompatActivity {

    private TextView deviceIdView;
    private TextView authTokenView;
    private TextView registeredLabelView;
    private Button deviceRegBtn;
    private Button pushRegBtn;
    private ProgressBar loadingBar;

    public ReplaySubject<String> firebaseTokenReplaySubject = ReplaySubject.create(1);

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String token = intent.getStringExtra(FirebaseIDService.FIREBASE_TOKEN);
            SharedPreferences.Editor editor = PreferenceManager
                    .getDefaultSharedPreferences(context).edit();
            editor.putString(FirebaseIDService.FIREBASE_TOKEN, token);
            editor.apply();
            firebaseTokenReplaySubject.onNext(token);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example);

        deviceRegBtn = (Button) findViewById(R.id.deviceRegBtn);
        pushRegBtn = (Button) findViewById(R.id.pushRegBtn);
        deviceIdView = (TextView) findViewById(R.id.deviceIdView);
        authTokenView = (TextView) findViewById(R.id.authTokenView);
        registeredLabelView = (TextView) findViewById(R.id.registeredLabelView);
        loadingBar = (ProgressBar) findViewById(R.id.loadingBar);

        setupObservers();

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(FirebaseIDService.FIREBASE_TOKEN));
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (preferences.contains(FirebaseIDService.FIREBASE_TOKEN)) {
            firebaseTokenReplaySubject
                    .onNext(preferences.getString(FirebaseIDService.FIREBASE_TOKEN, ""));
        }
    }

    private void setupObservers() {
        Observable<Object> regDeviceObservable = RxView.clicks(deviceRegBtn);
        Observable<Object> regPushObservable = RxView.clicks(pushRegBtn);
        ExampleViewModel viewModel = new ExampleViewModel(regDeviceObservable, regPushObservable,
                firebaseTokenReplaySubject, new ExampleController());

        viewModel.displayLoadingBarObservable.subscribe(
                isVisible -> loadingBar.setVisibility(isVisible ? View.VISIBLE : View.GONE));
        viewModel.devIdObservable.subscribe(did -> deviceIdView.setText(did));
        viewModel.authTokenObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(token -> authTokenView.setText(token));
        viewModel.deviceRegBtnObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> deviceRegBtn.setText(text));
        viewModel.pushRegBtnEnabledObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(isPushBtnEnabled -> pushRegBtn.setEnabled(isPushBtnEnabled));
        viewModel.pushRegBtnBgColorObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(
                colorInt -> pushRegBtn.setBackgroundColor(ContextCompat.getColor(this, colorInt)));
        viewModel.pushRegBtnObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> pushRegBtn.setText(text));
        viewModel.pushRegLabelBgColorObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(
                colorInt -> registeredLabelView.setBackgroundColor(ContextCompat.getColor(this, colorInt)));
        viewModel.pushRegLabelObservable.observeOn(AndroidSchedulers.mainThread())
                .subscribe(text -> registeredLabelView.setText(text));
    };
}
