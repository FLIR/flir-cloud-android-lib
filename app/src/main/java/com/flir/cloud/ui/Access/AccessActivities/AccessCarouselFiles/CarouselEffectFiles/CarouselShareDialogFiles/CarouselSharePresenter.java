package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselShareDialogFiles;


import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.models.Access.ShareResource;
import com.flir.sdk.models.Access.SharedUsersResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public class CarouselSharePresenter implements IPresenter {

    public static final int SHARE_DEVICE_SUCCESS_CODE = 1;
    public static final int UN_SHARE_DEVICE_SUCCESS_CODE = 0;

    private final CarouselShareView view;
    private CompositeDisposable subscriptions;
    private AccessInterceptor interceptor;


    public CarouselSharePresenter(AccessInterceptor interceptor, CarouselShareView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }

    public void postGetAllSharedDevices(String deviceId) {
        Observable<List<SharedUsersResponse>> observable= interceptor.getSharedResources(deviceId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<SharedUsersResponse>>() {
            @Override
            public void onComplete() {
                view.removeWait();
            }

            @Override
            public void onNext(List<SharedUsersResponse> response) {
                view.responseFromServer(response);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.onFailure(e.getMessage());
                view.removeWait();
            }

        }));
        view.showWait();

    }

    public void postSharedDevice(ShareResource aShareResource) {
        Observable<ResponseBody> observable= interceptor.postShareResource(aShareResource);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                view.removeWait();
            }

            @Override
            public void onNext(ResponseBody response) {
                view.responseFromServer(aShareResource.email, SHARE_DEVICE_SUCCESS_CODE);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SHARE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, aShareResource.id + "/" + aShareResource.email);
            }

            @Override
            public void onError(Throwable e) {
                view.onFailure(e.getMessage());
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SHARE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, aShareResource.id + "/" + aShareResource.email + "/" + e.getMessage());
            }

        }));
        view.showWait();

    }

    public void postUnSharedDevices(ShareResource aShareResource) {
        Observable<ResponseBody> observable= interceptor.postUnShareResource(aShareResource);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {
                view.removeWait();
            }

            @Override
            public void onNext(ResponseBody response) {
                view.responseFromServer(aShareResource.email, UN_SHARE_DEVICE_SUCCESS_CODE);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_UN_SHARE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, aShareResource.id + "/" + aShareResource.email);
            }

            @Override
            public void onError(Throwable e) {
                view.onFailure(e.getMessage());
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_UN_SHARE_DEVICE_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, aShareResource.id + "/" + aShareResource.email);
            }

        }));
        view.showWait();

    }





    public void onStop() {
        subscriptions.dispose();
    }
}
