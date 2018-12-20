package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.UpdateDeviceActivity;

import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.DeviceDetails;
import com.flir.sdk.models.Device.UpdateDevice;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti on 21-Jun-17.
 */

public class UpdateDevicePresenter implements IPresenter {

    public static final int RESPONSE_BODY_ACTION_DELETE = 0;
    public static final int RESPONSE_BODY_ACTION_SHARE = 1;

    private final IUpdateDeviceView view;
    private CompositeDisposable subscriptions;
    private DeviceInterceptor interceptor;


    public UpdateDevicePresenter(DeviceInterceptor interceptor, IUpdateDeviceView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }

    public void updateDevice(String deviceId, UpdateDevice updateDevice) {
        Observable<DeviceDetails> observable= interceptor.putUpdateDevice(deviceId,updateDevice);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<DeviceDetails>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(DeviceDetails deviceDetails) {
                view.responseFromServer(deviceDetails);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTING_UPDATE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, deviceId);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTING_UPDATE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, deviceId);
            }

        }));

        view.showWait();
    }

    public void getDevices() {
        Observable<List<DeviceDetails>> observable= interceptor.getDevices();
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<DeviceDetails>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<DeviceDetails> deviceList) {
                view.responseFromServer(deviceList);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }

}
