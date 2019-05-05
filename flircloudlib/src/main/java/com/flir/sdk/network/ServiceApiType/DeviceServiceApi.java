package com.flir.sdk.network.ServiceApiType;

import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Device.AddDevice;
import com.flir.sdk.models.Device.AddVirtualDevice;
import com.flir.sdk.models.Device.DeleteAttributes;
import com.flir.sdk.models.Device.DeviceDetails;
import com.flir.sdk.models.Device.DeviceSecret;
import com.flir.sdk.models.Device.GetDeviceSelfResponse;
import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.models.Device.GetPendingDevicesResponse;
import com.flir.sdk.models.Device.UpdateDevice;
import com.flir.sdk.models.Device.UpdateReported;
import com.flir.sdk.models.Device.UpdateState;
import com.flir.sdk.models.Device.UploadDownloadUrlResponse;

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

public interface DeviceServiceApi {


    @GET("/api/device/devices/self")
    Observable<GetDeviceSelfResponse> getSelf();

    @PUT("/api/device/devices/detach/{deviceID}")
    Observable<ResponseBody> detachDevice(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId);

    @PUT("/api/device/devices/update/{deviceID}")
    Observable<DeviceDetails> updateDevice(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId, @Body UpdateDevice updateDevice);

    @PUT("/api/device/devices/commit")
    Observable<ResponseBody> commitPendingDevice(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Body DeviceSecret aDeviceSecret);

    @POST("/api/device/devices")
    Observable<DeviceSecret> addPendingDevice(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Body AddDevice addDevice);

    @POST("/api/device/devices/{deviceLogicalID}/virtuals")
    Observable<GetDeviceSelfResponse> addVirtualDevice(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Path(value = "deviceLogicalID", encoded = true) String deviceLogicalID , @Body AddVirtualDevice addVirtualDevice );

    @DELETE("/api/device/devices/{deviceID}")
    Observable<ResponseBody> deleteDevice(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID") String deviceId);

    @GET("/api/device/devices")
    Observable<List<DeviceDetails>> getDevices(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id);

    @GET("/api/device/devices/pending")
    Observable<List<GetPendingDevicesResponse>>  getPendingDevices(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id);

    @GET("/api/device/devices/{serial}/information")
    Observable<DeviceDetails> getDevicesBySerial(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "serial", encoded = true) String serial);

    @GET("/api/device/{deviceID}/state")
    Observable<List<GetDeviceStateResponse>> getDeviceState(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId ,@Query("channel") String channel);

    @POST("/api/device/{deviceID}/state/desired")
    Observable<ResponseBody> updateDesired(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Header(Constants.CONTENT_TYPE_KEY) String content_type, @Path(value = "deviceID", encoded = true) String deviceId, @Body List<UpdateState> updateDesiredList);

    @POST("/api/device/{deviceID}/state/reported")
    Observable<ResponseBody> updateReported(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Path(value = "deviceID", encoded = true) String deviceId, @Body List<UpdateReported> updateDesiredList , @Query("override") String isOverride);

    @DELETE("/api/device/{deviceID}/state/reported")
    Observable<ResponseBody> DeleteAttributes(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Path(value = "deviceID", encoded = true) String deviceId, @Body List<DeleteAttributes> deleteAttributesList);

    @GET("/api/device/{deviceID}/upload/{attribute}")
    Observable<UploadDownloadUrlResponse> presignedUploadUrl(@Path(value = "deviceID", encoded = true) String deviceId, @Path(value = "attribute", encoded = true) String attribute ,@Query("channel") String channel);

    @GET("/api/device/{deviceID}/download/{attribute}")
    Observable<UploadDownloadUrlResponse> downloadFile(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "deviceID", encoded = true) String deviceId, @Path(value = "attribute", encoded = true) String attribute ,@Query("channel") String channel);

}
