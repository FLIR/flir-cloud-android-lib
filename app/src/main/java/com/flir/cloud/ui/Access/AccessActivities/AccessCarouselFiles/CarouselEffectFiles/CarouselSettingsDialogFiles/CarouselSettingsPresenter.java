package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles;


import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.UpdateState;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.ResponseBody;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public class CarouselSettingsPresenter implements IPresenter {

    private final CarouselSettingsView view;
    private CompositeDisposable subscriptions;
    private DeviceInterceptor interceptor;


    public CarouselSettingsPresenter(DeviceInterceptor interceptor, CarouselSettingsView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
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

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_UPDATE_DESIRED_MADE,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, updateDesiredList.get(0).key + "/" + updateDesiredList.get(0).value);
            }

            @Override
            public void onError(Throwable e) {
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_UPDATE_DESIRED_MADE,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, updateDesiredList.get(0).key + "/" + updateDesiredList.get(0).value);
            }

        }));

    }





    public void onStop() {
        subscriptions.dispose();
    }
}
