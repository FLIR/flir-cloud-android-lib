package com.flir.cloud.ui.fileExplorer.VolumePageClasses;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.StorageInterceptor;
import com.flir.sdk.models.Storage.UpdateVolume;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.VolumeDetails;
import com.flir.sdk.models.Storage.VolumeResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class VolumeMainPagePresenter {
    
    private final VolumeMainPageView view;
    private CompositeDisposable subscriptions;
    private StorageInterceptor interceptor;


    public VolumeMainPagePresenter(StorageInterceptor interceptor, VolumeMainPageView view) {
        this.view = view;
        this.interceptor = interceptor;
        subscriptions = new CompositeDisposable();
    }

    public void postGetAllResources() {
        Observable<List<VolumeResponse>> observable= interceptor.getVolumes();
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<VolumeResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<VolumeResponse> response) {
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

    public void postAddVolumeResource(VolumeDetails volumeDetails) {
        Observable<VolumeResponse> observable= interceptor.createVolume(volumeDetails);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<VolumeResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(VolumeResponse response) {
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_CREATED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, response.VolumeName);

                view.addVolumePosition(response);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_CREATED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, e.getMessage());

                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }

    public void getVolumeFiles(String volume, int pageSize) {
        Observable<VolumeFilesResponse> observable= interceptor.getVolumeFiles(volume, pageSize, null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<VolumeFilesResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(VolumeFilesResponse response) {
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


    public void putUpdateVolume(String serial, UpdateVolume updateResource) {
        Observable<VolumeResponse> observable= interceptor.updateVolume(updateResource, serial);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<VolumeResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(VolumeResponse response) {
                view.updateVolumePosition(response,serial);
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

    public void deleteVolumeResource(String serial) {
       /* Observable<ResponseBody> observable= interceptor.deleteResource(id);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<ResponseBody>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(ResponseBody responseBody) {
                view.removeVolumePosition(id);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();*/
    }

    public void onStop() {
        subscriptions.dispose();
    }
}
