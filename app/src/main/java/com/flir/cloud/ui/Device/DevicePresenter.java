package com.flir.cloud.ui.Device;


import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.models.Device.UpdateState;
import com.flir.sdk.models.Device.UploadDownloadUrlResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public class DevicePresenter implements IPresenter {
    private final DeviceView view;
    private CompositeDisposable subscriptions;
    private DeviceInterceptor interceptor;


    public DevicePresenter(DeviceInterceptor interceptor, DeviceView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }


    public void getDeviceState(String deviceId) {
        Observable<List<GetDeviceStateResponse>> observable= interceptor.getDeviceState(deviceId, null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<GetDeviceStateResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<GetDeviceStateResponse> response) {
                view.responseDeviceStateListFromServer(response);
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

    public void postUpdateDesired(String deviceId, List<UpdateState> updateDesiredList) {
        Observable<ResponseBody> observable= interceptor.postUpdateDesired(deviceId, updateDesiredList);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResponseBody response) {
                view.responseFromServer();
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

    public void getPresignedUploadUrl(String deviceId, String attribute) {
        Observable<UploadDownloadUrlResponse> observable= interceptor.getPresignedUploadUrl(deviceId, attribute,null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<UploadDownloadUrlResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(UploadDownloadUrlResponse response) {
                view.responseFromServer(response);
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

    public void getDownloadFile(String deviceId, String attribute) {

        Observable<UploadDownloadUrlResponse> observable= interceptor.getDownloadFile(deviceId, attribute, null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<UploadDownloadUrlResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(UploadDownloadUrlResponse response) {
                view.responseFromServer(response);
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

    public void onStop() {
        subscriptions.dispose();
    }
}
