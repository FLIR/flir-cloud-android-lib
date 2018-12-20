package com.flir.sdk.network.ServiceApiType;

import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Playback.ClipsResponse;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Moti on 15-May-17.
 */

public interface PlaybackServiceApi {

    @GET("/api/playback/export")
    Observable<PlaybackUrlResponse> getExportVideoUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String deviceId, @Query("stream") String streamType, @Query("start_time") String startTime, @Query("end_time") String endTime);

    @GET
    @Streaming
    Call<ResponseBody> downloadExportFile(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Url String url);

    @GET("/api/playback/clips")
    Observable<List<ClipsResponse>> getClips(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String deviceId, @Query("stream") String stream, @Query("start_time") String startTime, @Query("end_time") String endTime);

    @GET("/api/playback/events")
    Observable<List<ClipsResponse>> getEvents(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String deviceId, @Query("stream") String stream, @Query("start_time") String startTime, @Query("end_time") String endTime);

    @GET("/api/playback/playback-url")
    Observable<PlaybackUrlResponse> getPlaybackUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String deviceId, @Query("stream") String streamFormat);

    @GET("/api/playback/stream")
    Observable<PlaybackUrlResponse> getPlaybackStream(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id,  @Query("serial") String deviceId, @Query("stream") String stream ,@Query("start_time") String startTime);

    @GET("/api/playback/thumbnail")
    Observable<PlaybackUrlResponse> getPlaybackThumbnail(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("serial") String deviceId, @Query("stream") String stream, @Query("start_time") String startTime, @Query("end_time") String endTime);

}
