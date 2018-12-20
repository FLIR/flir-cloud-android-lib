package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraVideoView;


import com.flir.sdk.models.Events.GetUrlResponse;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface ICameraVideoItemView {

    void responseFromServer(GetUrlResponse aGetUrlResponse);

    void onFailure(String reqName);

}
