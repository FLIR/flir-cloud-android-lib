package com.flir.cloud.ui.Access;


import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Access.AddResource;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.ShareResource;
import com.flir.sdk.models.Access.SharedUsersResponse;
import com.flir.sdk.models.Device.DeviceDetails;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public class AccessPresenter implements IPresenter {
    private final AccessView view;
    private CompositeDisposable subscriptions;
    private AccessInterceptor interceptor;
    private DeviceInterceptor deviceInterceptor;


    public AccessPresenter(AccessInterceptor interceptor,DeviceInterceptor aDeviceInterceptor, AccessView view) {
        this.view = view;
        this.interceptor = interceptor;
        this.deviceInterceptor = aDeviceInterceptor;
        subscriptions = new CompositeDisposable();
    }


    public void postAddResource(AddResource addResource) {
        Observable<ResourceResponse> observable= interceptor.postAddResource(addResource);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResourceResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResourceResponse response) {
                view.responseFromServer(response);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_DEVICE_SELECTION_CAROUSEL_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ADD_DEVICE_BUTTON_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, addResource.id + "/" + addResource.name);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_DEVICE_SELECTION_CAROUSEL_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ADD_DEVICE_BUTTON_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, addResource.id + "/" + addResource.name + "  Error Message = " + e.getMessage());

            }

        }));

        view.showWait();
    }

    public void postGetSingleResource(String serial) {
        Observable<ResourceResponse> observable= interceptor.getGetSingleResource(serial);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResourceResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResourceResponse response) {
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

    public void deleteResource(String serial) {
        Observable<ResponseBody> observable= interceptor.deleteResource(serial);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
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

    public void postGetAllDevices() {
        Observable<List<DeviceDetails>> observable= deviceInterceptor.getDevices();
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<DeviceDetails>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<DeviceDetails> response) {
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

    public void putUpdateResource(AddResource updateResource) {
        Observable<ResourceResponse> observable= interceptor.putUpdateResource(updateResource);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResourceResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResourceResponse response) {
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

    public void postShareResource(ShareResource shareResource) {
        Observable<ResponseBody> observable= interceptor.postShareResource(shareResource);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
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

    public void postUnShareResource(ShareResource shareDevice) {
        Observable<ResponseBody> observable= interceptor.postUnShareResource(shareDevice);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
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

    public void getAllSharedResources(String serial) {
        Observable<List<SharedUsersResponse>> observable= interceptor.getSharedResources(serial);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<SharedUsersResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<SharedUsersResponse> response) {
                view.responseListFromServer(response);
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
