package com.flir.sdk.Interceptors;


import com.flir.sdk.models.Playback.ClipsResponse;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.PlaybackServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by moti on 16/03/2017.
 */

public class PlaybackInterceptor {

    private PlaybackServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private AuthenticationProvider authenticationProvider;

    public PlaybackInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (PlaybackServiceApi) serviceGenerator.createService(PlaybackServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.authenticationProvider = authenticationProvider;
    }

    public Observable<PlaybackUrlResponse> getExportVideoUrl(String serial, String streamType, String startTime, String endTime) {
        return serviceApi.getExportVideoUrl(authenticationProvider.getAccountToken(), serial, streamType, startTime, endTime)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Call<ResponseBody> downloadExportFile(String contentType, String downloadUrl) {
        return serviceApi.downloadExportFile(contentType, downloadUrl);
    }

    public Observable<List<ClipsResponse>> getClips(String serial, String stream, String startTime, String endTime) {
        return serviceApi.getClips(authenticationProvider.getAccountToken(), serial, stream, startTime, endTime)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<List<ClipsResponse>> getEvents(String serial, String streamType, String startTime, String endTime) {
        return serviceApi.getEvents(authenticationProvider.getAccountToken(), serial, streamType, startTime, endTime)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<PlaybackUrlResponse> getPlayback(String serial, String streamFormat) {
        return serviceApi.getPlaybackUrl(authenticationProvider.getAccountToken(), serial, streamFormat)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<PlaybackUrlResponse> getPlaybackStreamUrl(String serial, String streamType, String startTime) {
        return serviceApi.getPlaybackStream(authenticationProvider.getAccountToken(), serial, streamType, startTime)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<PlaybackUrlResponse> getPlaybackThumbnail(String serial, String stream, String startTime, String endTime) {
        return serviceApi.getPlaybackThumbnail(authenticationProvider.getAccountToken(), serial, stream, startTime, endTime)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

}
