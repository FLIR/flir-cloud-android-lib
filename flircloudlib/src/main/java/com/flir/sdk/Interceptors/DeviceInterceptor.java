package com.flir.sdk.Interceptors;


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
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.DeviceServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okhttp3.ResponseBody;
import retrofit2.http.OPTIONS;

/**
 * Created by moti on 16/03/2017.
 */

public class DeviceInterceptor {

    private DeviceServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private AuthenticationProvider authenticationProvider;

    public DeviceInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (DeviceServiceApi) serviceGenerator.createService(DeviceServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.authenticationProvider = authenticationProvider;
    }

    public Observable<List<GetDeviceStateResponse>> getDeviceState(String deviceId, String channel) {
        return serviceApi.getDeviceState(authenticationProvider.getAccountToken(), deviceId,channel)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> postUpdateDesired(String deviceId, List<UpdateState> updateDesiredList) {
        return serviceApi.updateDesired(authenticationProvider.getAccountToken(),Constants.CONTENT_TYPE, deviceId, updateDesiredList)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> postUpdateReported(String deviceId, List<UpdateReported> updateReportedList, boolean isOverride) {
        return serviceApi.updateReported(Constants.CONTENT_TYPE, deviceId, updateReportedList, Boolean.toString(isOverride))
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> deleteAttributes(String deviceId, List<DeleteAttributes> deleteAttributesList) {
        return serviceApi.DeleteAttributes(Constants.CONTENT_TYPE, deviceId, deleteAttributesList)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<UploadDownloadUrlResponse> getPresignedUploadUrl(String deviceId, String attribute ,String channel) {
        return serviceApi.presignedUploadUrl(deviceId, attribute, channel)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<UploadDownloadUrlResponse> getDownloadFile(String deviceId, String attribute ,String channel) {
        return serviceApi.downloadFile(authenticationProvider.getAccountToken(), deviceId, attribute, channel)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }



    public Observable<DeviceSecret> postPendingDevice(AddDevice addDevice) {
        return serviceApi.addPendingDevice(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken(), addDevice)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetDeviceSelfResponse> addVirtualDevice(String deviceLogicalID, AddVirtualDevice addVirtualDevice) {
        return serviceApi.addVirtualDevice(Constants.CONTENT_TYPE, deviceLogicalID, addVirtualDevice)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> commitPendingDevice(DeviceSecret aDeviceSecret) {
        return serviceApi.commitPendingDevice(Constants.CONTENT_TYPE, aDeviceSecret)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetDeviceSelfResponse> getSelf() {
        return serviceApi.getSelf()
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> detachDevice(String deviceId) {
        return serviceApi.detachDevice(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken(), deviceId)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<DeviceDetails> putUpdateDevice(String deviceId, UpdateDevice updateDevice) {
        return serviceApi.updateDevice(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken(), deviceId, updateDevice)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<List<DeviceDetails>> getDevices() {
        return serviceApi.getDevices(authenticationProvider.getAccountToken())
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }


    public Observable<List<GetPendingDevicesResponse>> getPendingDevices() {
        return serviceApi.getPendingDevices(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken())
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<DeviceDetails> getDeviceBySerial(String serial) {
        return serviceApi.getDevicesBySerial(authenticationProvider.getAccountToken(), serial)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<ResponseBody> deleteDevice(String serial) {
        return serviceApi.deleteDevice(authenticationProvider.getAccountToken() ,serial)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

}
