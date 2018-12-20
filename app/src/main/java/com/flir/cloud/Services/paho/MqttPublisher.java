/*
 * Copyright 2016 The Android Open Source Project and others
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 *      Benjamin Cabé <benjamin@eclipse.org> - Adapt PubSubPublisher for MQTT
 *
 */

package com.flir.cloud.Services.paho;

import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.flir.cloud.MainApplication;
import com.flir.cloud.Services.MqttTopicSubject;
import com.flir.cloud.Services.MqttTopicType;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.GetDeviceStateResponse;

import org.eclipse.paho.client.mqttv3.DisconnectedBufferOptions;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

public class MqttPublisher implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static MqttPublisher INSTANCE;

    public static final String MQTT_FILE_MESSAGE_SUBJECT_CREATE = "Create";
    public static final String MQTT_FILE_MESSAGE_SUBJECT_DELETE = "Delete";
    public static final String MQTT_FILE_MESSAGE_SUBJECT_UPDATE = "Update";

    private static void init() {
        if (INSTANCE == null) {
            try {
                INSTANCE = new MqttPublisher();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static MqttPublisher getInstance() {
        if(INSTANCE == null){
            init();
        }
        return INSTANCE;
    }

    @Inject
    DeviceInterceptor deviceInterceptor;

    private LambdaSharedPreferenceManager lambdaSharedPreferenceManager;

    private static final String TAG = "MqttPublisher";

    private final String mqttServerUri;

    private Handler mHandler;
    private HandlerThread mHandlerThread;
    private CompositeDisposable subscriptions;
    private boolean addDebugTopic = false;

    private static final long PUBLISH_INTERVAL_MS = TimeUnit.SECONDS.toMillis(1);

    private MqttWebSocketAsyncClient mMqttAndroidClient;

    public MqttPublisher() throws IOException {
        ((MainApplication)MainApplication.getAppContext()).getApplicationComponent().inject(this);
        subscriptions = new CompositeDisposable();
        lambdaSharedPreferenceManager = LambdaSharedPreferenceManager.getInstance();
        lambdaSharedPreferenceManager.getLambdaSharedPreference().registerOnSharedPreferenceChangeListener(this);
        mqttServerUri = lambdaSharedPreferenceManager.getLambdaPrefsValue(LambdaSharedPreferenceManager.MQTT_URL_CONNECTION, "urlNotExist");
        mHandlerThread = new HandlerThread("mqttPublisherThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper());
        try {
            mMqttAndroidClient = new MqttWebSocketAsyncClient(mqttServerUri, MqttClient.generateClientId(),new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }

        mMqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.d(TAG, "MQTT connection complete");

                addDebugTopics(addDebugTopic);
                updateDevicesMqttSubscriber();
                updateVolumesMqttSubscriber();
            }

            @Override
            public void connectionLost(Throwable cause) {
                 Log.d(TAG, "MQTT connection lost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String [] topicPieces = topic.split("/");
                if(topicPieces.length > 2) {
                    String serial = topicPieces[topicPieces.length - 3];
                    String type = topicPieces[topicPieces.length - 2];
                    String subject = topicPieces[topicPieces.length - 1];
                    messageArrivedAction(serial, type, subject);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                Log.d(TAG, "MQTT delivery complete");
            }
        });

        final MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setKeepAliveInterval(20);
        //mqttConnectOptions.setCleanSession(true);

        mHandler.post(() -> {
        // Connect to the broker
            try {
                mMqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken) {
                        DisconnectedBufferOptions disconnectedBufferOptions = new DisconnectedBufferOptions();
                        disconnectedBufferOptions.setBufferEnabled(true);
                        disconnectedBufferOptions.setBufferSize(5000);
                        disconnectedBufferOptions.setPersistBuffer(false);
                        disconnectedBufferOptions.setDeleteOldestMessages(false);
                        mMqttAndroidClient.setBufferOpts(disconnectedBufferOptions);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                        Log.d(TAG, "MQTT connection failure", exception);
                        //attemptToReconnect();
                    }
                });
            } catch (MqttException e) {
                Log.d(TAG, "MQTT connection failure", e);
                //attemptToReconnect();

            }

        });
    }

    private void updateDevicesMqttSubscriber() {
        if(mMqttAndroidClient.isConnected()) {
            String[] deviceList = lambdaSharedPreferenceManager.getDeviceList();
            for (String deviceItemName : deviceList) {
                try {
                    if(!deviceItemName.isEmpty()) {
                        mMqttAndroidClient.subscribe(deviceItemName + "/#", 0);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void updateVolumesMqttSubscriber() {
        if(mMqttAndroidClient.isConnected()) {
            String[] volumeList = lambdaSharedPreferenceManager.getVolumeList();
            for (String volumeItemName : volumeList) {
                try {
                    if(!volumeItemName.isEmpty()) {
                        mMqttAndroidClient.subscribe(volumeItemName + "/#", 0);
                    }
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void messageArrivedAction(String serial, String type, String subject) {
        //Log.d("Mqtt", "messageArrivedAction - id = " + id +"  type = " + type + "  subject = " + subject);
        //Update Topic Type
        if (type.equals(MqttTopicType.MQTT_TOPIC_TYPE_UPDATE.getCode())) {
            if (subject.equals(MqttTopicSubject.MQTT_TOPIC_SUBJECT_STATE.getCode())) {
                getDeviceState(serial);
            }

        }
        //Live Topic Type
        else if(type.equals(MqttTopicType.MQTT_TOPIC_TYPE_LIVE.getCode())) {
            if (subject.equals(MqttTopicSubject.MQTT_TOPIC_SUBJECT_DEVICE.getCode())) {

            }
        }
        //File Topic Type
        else if(type.equals(MqttTopicType.MQTT_TOPIC_TYPE_FILE.getCode())) {
            setSharedPreferenceFileTopic(serial, type, subject);
        }

    }

    private void setSharedPreferenceFileTopic(String serial, String type, String subject) {
        lambdaSharedPreferenceManager.setLambdaPrefsValue(type + "/" +subject ,  serial);
    }

    public MqttWebSocketAsyncClient getMqttAndroidClient() {
        return mMqttAndroidClient;
    }

    private void addDebugTopics(boolean addDebugTopic) {
        
        if(addDebugTopic) {
            try {
                mMqttAndroidClient.subscribe("moti-cam-1" + "/#", 0);
                mMqttAndroidClient.subscribe("moti-cam-2" + "/#", 0);
                mMqttAndroidClient.subscribe("moti-cam-3" + "/#", 0);
            } catch (MqttException e) {
                e.printStackTrace();
            }
        }
    }


    private void getDeviceState(String deviceId) {
        Observable<List<GetDeviceStateResponse>> observable = deviceInterceptor.getDeviceState(deviceId, null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<GetDeviceStateResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<GetDeviceStateResponse> response) {
                for(int i = 0 ; i< response.size(); i++){
                    lambdaSharedPreferenceManager.setStateBySerialAndKey(response.get(i));
                }
            }

            @Override
            public void onError(Throwable e) {

            }

        }));

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.equals(LambdaSharedPreferenceManager.DEVICES_LIST_IDS)){
            updateDevicesMqttSubscriber();
        }

        if(key.equals(LambdaSharedPreferenceManager.VOLUMES_LIST_IDS)){
            updateVolumesMqttSubscriber();
        }
    }
}