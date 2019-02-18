package com.flir.sdk.Interceptors;


import com.flir.sdk.models.Events.GetUrlResponse;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.StreamingServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import io.reactivex.Observable;
import io.reactivex.Scheduler;

/**
 * Created by moti on 16/03/2017.
 */

public class StreamingInterceptor {

    private StreamingServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private AuthenticationProvider authenticationProvider;

    public StreamingInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (StreamingServiceApi) serviceGenerator.createService(StreamingServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.authenticationProvider = authenticationProvider;
    }

    public Observable<GetUrlResponse> getDownloadVideoUrl(String protocol, String serial, String contentType, String stream) {
        return serviceApi.getDownloadVideoUrl(authenticationProvider.getAccountToken(), protocol, serial,contentType ,stream)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetUrlResponse> getUploadVideoUrl(String serial, String stream, String contentType) {
        return serviceApi.getUploadVideoUrl(authenticationProvider.getAccountToken(), serial, stream, contentType)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetUrlResponse> getUploadAudioUrl(String serial, String stream, String contentType) {
        return serviceApi.getUploadAudioUrl(authenticationProvider.getAccountToken(), serial, stream, contentType)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetUrlResponse> getDownloadAudioUrl(String serial, String stream, String contentType) {
        return serviceApi.getDownloadAudioUrl(authenticationProvider.getAccountToken(), serial, stream, contentType)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

}
