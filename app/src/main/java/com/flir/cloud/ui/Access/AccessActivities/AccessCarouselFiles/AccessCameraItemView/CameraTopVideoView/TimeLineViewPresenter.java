package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView;


import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.PlaybackInterceptor;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti on 10-Sep-17.
 */

public class TimeLineViewPresenter {


    private final TimeLineSelectorLinearLayout view;
    private CompositeDisposable subscriptions;
    private PlaybackInterceptor interceptor;


    public TimeLineViewPresenter(PlaybackInterceptor interceptor, TimeLineSelectorLinearLayout view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }

    public void getExportVideo(String serial, String streamType, String startTime, String endTime) {
        Observable<PlaybackUrlResponse> observable= interceptor.getExportVideoUrl(serial, streamType, startTime, endTime);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<PlaybackUrlResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(PlaybackUrlResponse aPlaybackUrlResponse) {
                view.response(aPlaybackUrlResponse, startTime, endTime);

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_EXPORT_VIDEO_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, startTime + "/" + endTime);
            }

            @Override
            public void onError(Throwable e) {
               view.onFailure(e.getMessage());
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_EXPORT_VIDEO_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, startTime + "/" + endTime + "/" + e.getMessage());
            }

        }));

    }
}
