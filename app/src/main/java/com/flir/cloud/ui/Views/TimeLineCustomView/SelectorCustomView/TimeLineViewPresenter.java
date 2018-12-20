package com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView;


import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.PlaybackInterceptor;
import com.flir.sdk.models.Playback.ClipsResponse;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti on 10-Sep-17.
 */

public class TimeLineViewPresenter {


    private final ISelectorView view;
    private CompositeDisposable subscriptions;
    private PlaybackInterceptor interceptor;


    public TimeLineViewPresenter(PlaybackInterceptor interceptor, ISelectorView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }

    public void getPlaybackStreamUrl(String serial, String streamType, String time) {
        Observable<PlaybackUrlResponse> observable= interceptor.getPlaybackStreamUrl(serial, streamType, time);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<PlaybackUrlResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(PlaybackUrlResponse aPlaybackUrlResponse) {
                view.response(aPlaybackUrlResponse);

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_TIME_LINE_VIEW_TAPED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, serial + "/" + time);
            }

            @Override
            public void onError(Throwable e) {
            //    view.onFailure(e.getMessage());
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_TIME_LINE_VIEW_TAPED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, serial + "/" + time +  "/" + e.getMessage());
            }

        }));

    }

    public void getPlaybackClips(String serial, String stream, String start, String end) {
        Observable<List<ClipsResponse>> observable= interceptor.getClips(serial, stream, start, end);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<ClipsResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<ClipsResponse> aClipsResponse) {
                view.response(aClipsResponse);
            }

            @Override
            public void onError(Throwable e) {
                //    view.onFailure(e.getMessage());
            }

        }));

    }
}
