package com.flir.sdk.network.ServiceApiType;


import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Events.GetUrlResponse;
import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Header;

/**
 * Created by Moti on 15-May-17.
 */

public interface EventsServiceApi {

    @GET("/api/events/mqtt-url")
    Observable<GetUrlResponse> getMqttUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id);

}
