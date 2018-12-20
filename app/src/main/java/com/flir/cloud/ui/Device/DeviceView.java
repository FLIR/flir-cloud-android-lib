package com.flir.cloud.ui.Device;

import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.models.Device.UploadDownloadUrlResponse;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface DeviceView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer();

    void responseDeviceStateListFromServer(List<GetDeviceStateResponse> responses);

    void responseFromServer(UploadDownloadUrlResponse responses);

    void responseFromServer(ResponseBody responses);


}
