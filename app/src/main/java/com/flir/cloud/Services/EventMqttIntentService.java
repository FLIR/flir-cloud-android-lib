package com.flir.cloud.Services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.flir.cloud.MainApplication;
import com.flir.cloud.Services.paho.MqttPublisher;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.sdk.Interceptors.EventsInterceptor;
import com.flir.sdk.models.Events.GetUrlResponse;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;


/**
 * Created by Moti on 07-Jun-17.
 */

public class EventMqttIntentService extends IntentService{

    public static final int EVENT_SERVICE_RETRY_IN_SEC = 5;

    @Inject
    EventsInterceptor eventsInterceptor;

    private MqttPublisher mMqttPublisher;

    private String eventUrl;


    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        Log.d(getPackageName(), "Created MyResultsService");
    }

    public EventMqttIntentService() {
        // Used to name the worker thread
        // Important only for debugging
        super(EventMqttIntentService.class.getName());
      //  setIntentRedelivert(true);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Invoked on the worker thread
        // Do some work in background without affecting the UI thread
        getEventUrl();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    public void getEventUrl() {
        Observable<GetUrlResponse> observable = eventsInterceptor.getMqttUrl();
        CompositeDisposable subscriptions = new CompositeDisposable();
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetUrlResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetUrlResponse mqttUrlResponse) {
                eventUrl = mqttUrlResponse.url;
                startListening(eventUrl);
            }

            @Override
            public void onError(Throwable e) {

                ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
                Runnable task = () -> getEventUrl();
                worker.schedule(task, EVENT_SERVICE_RETRY_IN_SEC, TimeUnit.SECONDS);
                Log.d("MNMN", "Service onError");
            }

        }));
    }


    private void startListening(String eventUrl) {

            LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.MQTT_URL_CONNECTION, eventUrl);
            mMqttPublisher =  MqttPublisher.getInstance();
    }




}
