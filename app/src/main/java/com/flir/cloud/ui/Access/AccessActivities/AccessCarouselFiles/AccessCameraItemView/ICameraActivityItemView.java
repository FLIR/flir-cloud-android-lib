package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;


import com.flir.sdk.models.Device.GetDeviceStateResponse;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface ICameraActivityItemView {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer(int action);

    void responseFromServer(List<GetDeviceStateResponse> response, boolean isShowDialogWithDetails);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);

    void onUploadAudioUrlSuccess(String url) throws URISyntaxException;

    void onUploadAudioUrlFailure();

}
