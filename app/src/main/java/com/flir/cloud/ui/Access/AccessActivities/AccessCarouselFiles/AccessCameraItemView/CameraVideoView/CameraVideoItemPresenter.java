package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraVideoView;

import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.sdk.Interceptors.StreamingInterceptor;
import com.flir.sdk.models.Events.GetUrlResponse;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti on 21-Jun-17.
 */

public class CameraVideoItemPresenter implements IPresenter {

    public static final String CAMERA_VIDEO_ITEM_PRESENTER_GET_DOWNLOAD_VIDEO_URL = "GetDownloadVideoUrl";
    private final ICameraVideoItemView view;
    private CompositeDisposable subscriptions;
    private StreamingInterceptor mStreamingInterceptor;


    public CameraVideoItemPresenter(StreamingInterceptor aStreamingInterceptor, ICameraVideoItemView view) {
        this.view = view;
        this.mStreamingInterceptor = aStreamingInterceptor;
        subscriptions = new CompositeDisposable();
    }

    public void getDownloadVideoUrl(String protocol, String serial, String contentType, String stream) {
        Observable<GetUrlResponse> observable= mStreamingInterceptor.getDownloadVideoUrl(protocol, serial, contentType, stream);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetUrlResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetUrlResponse aGetUrlResponse) {
                view.responseFromServer(aGetUrlResponse);
            }

            @Override
            public void onError(Throwable e) {
                view.onFailure(CAMERA_VIDEO_ITEM_PRESENTER_GET_DOWNLOAD_VIDEO_URL);
            }

        }));

    }
}
