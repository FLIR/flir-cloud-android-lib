package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView;

import com.flir.sdk.models.Playback.PlaybackUrlResponse;

/**
 * Created by Moti on 10-Sep-17.
 */

public interface ITopVideoViewSelectorView {

     void response(PlaybackUrlResponse aPlaybackUrlResponse,String startTime, String endTime);

     void onFailure(String aMessage);

     void sendEvent(String aCategory, String aAction, String aEvent, String aComment);

}
