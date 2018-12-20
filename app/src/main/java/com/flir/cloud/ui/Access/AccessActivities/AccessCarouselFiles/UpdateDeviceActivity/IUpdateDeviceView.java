package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.UpdateDeviceActivity;


import com.flir.sdk.models.Device.AddDevice;
import com.flir.sdk.models.Device.DeviceDetails;

import java.util.List;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface IUpdateDeviceView {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer(AddDevice device);

    void responseFromServer(List<DeviceDetails> deviceList);

    void responseFromServer(DeviceDetails deviceDetails);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);

}
