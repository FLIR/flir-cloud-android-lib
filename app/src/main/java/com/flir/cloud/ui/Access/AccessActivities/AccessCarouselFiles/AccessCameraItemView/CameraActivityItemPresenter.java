package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;

import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.GetDeviceStateResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * Created by Moti on 21-Jun-17.
 */

public class CameraActivityItemPresenter implements IPresenter {

    public static final int RESPONSE_BODY_ACTION_DELETE = 0;
    public static final int RESPONSE_BODY_ACTION_SHARE = 1;

    private final ICameraActivityItemView view;
    private CompositeDisposable subscriptions;
    private DeviceInterceptor interceptor;


    public CameraActivityItemPresenter(DeviceInterceptor interceptor, ICameraActivityItemView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }

    public void deleteResource(String serial) {
        Observable<ResponseBody> observable= interceptor.deleteDevice(serial);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                view.responseFromServer(RESPONSE_BODY_ACTION_DELETE);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_DELETE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, serial);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_DELETE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, serial + "/" + e.getMessage());
            }

        }));

        view.showWait();
    }

    public void getDeviceState(String deviceId, boolean isShowDialogWithDetails) {
        Observable<List<GetDeviceStateResponse>> observable= interceptor.getDeviceState(deviceId,null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<GetDeviceStateResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<GetDeviceStateResponse> response) {
                view.responseFromServer(response, isShowDialogWithDetails);
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
