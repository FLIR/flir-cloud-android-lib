package com.flir.sdk.network.ServiceApiType;


import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Events.GetUrlResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;

/**
 * Created by Moti on 15-May-17.
 */

public interface StreamingServiceApi {

    //Video URL
    @GET("/api/streaming/upload-url")
    Observable<GetUrlResponse> getUploadVideoUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String serial, @Query("stream") String stream, @Query("contentType") String contentType);

    @GET("/api/streaming/download-url")
    Observable<GetUrlResponse> getDownloadVideoUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("protocol") String protocol, @Query("serial") String serial,@Query("contentType") String contentType, @Query("stream") String stream);

    //Out URL
    @GET("/api/streaming/out/upload-url")
    Observable<GetUrlResponse> getUploadAudioUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String serial, @Query("stream") String stream, @Query("contentType") String contentType);

    @GET("/api/streaming/out/download-url")
    Observable<GetUrlResponse> getDownloadAudioUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String serial, @Query("stream") String stream, @Query("contentType") String contentType);

}
