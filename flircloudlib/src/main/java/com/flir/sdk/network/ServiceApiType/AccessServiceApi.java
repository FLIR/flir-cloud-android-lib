package com.flir.sdk.network.ServiceApiType;

import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Access.AddResource;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.SharedUsersResponse;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Moti on 15-May-17.
 */

public interface AccessServiceApi {


    @POST("/api/access/resources")
    Observable<ResourceResponse> addResource(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Body AddResource addDevice);

    @GET("/api/access/resources/{deviceID}")
    Observable<ResourceResponse> getSingleResource(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId);

    @GET("/api/access/resources")
    Observable<List<ResourceResponse>> getAllResources(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id);

    @PUT("/api/access/resources/{deviceID}")
    Observable<ResourceResponse> updateResource(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId, @Body AddResource addResource);

    @DELETE("/api/access/resources/{deviceID}")
    Observable<ResponseBody> deleteResource(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId);

    @PUT("/api/access/acl/users")
    Observable<ResponseBody> shareResource(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("id") String deviceId, @Query("email") String email);

    @DELETE("/api/access/acl/users")
    Observable<ResponseBody> unShareResource(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("id") String deviceId, @Query("email") String email);

    @GET("/api/access/acl/users")
    Observable<List<SharedUsersResponse>> getSharedResources(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("id") String deviceId);
}
