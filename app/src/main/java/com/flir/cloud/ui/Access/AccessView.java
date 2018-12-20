package com.flir.cloud.ui.Access;

import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.SharedUsersResponse;
import com.flir.sdk.models.Device.DeviceDetails;

import java.util.List;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface AccessView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer();

    void responseFromServer(ResourceResponse response);

    void responseFromServer(List<DeviceDetails> response);

    void responseListFromServer(List<SharedUsersResponse> response);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);
}
