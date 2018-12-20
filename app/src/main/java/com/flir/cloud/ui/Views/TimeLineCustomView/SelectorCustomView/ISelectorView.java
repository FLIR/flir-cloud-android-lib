package com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView;

import com.flir.sdk.models.Playback.ClipsResponse;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;

import java.util.List;

/**
 * Created by Moti on 10-Sep-17.
 */

public interface ISelectorView {

     void response(PlaybackUrlResponse aPlaybackUrlResponse);

     void response(List<ClipsResponse> aClipsResponse);

     void sendEvent(String aCategory, String aAction, String aEvent, String aComment);
}
