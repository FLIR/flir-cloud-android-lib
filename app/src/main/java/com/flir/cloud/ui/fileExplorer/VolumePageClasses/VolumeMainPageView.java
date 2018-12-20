package com.flir.cloud.ui.fileExplorer.VolumePageClasses;

import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.VolumeResponse;

import java.util.List;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface VolumeMainPageView {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer(List<VolumeResponse> response);

    void responseFromServer(VolumeFilesResponse response);

    void addVolumePosition(VolumeResponse aVolumeResponse);

    void updateVolumePosition(VolumeResponse response, String serial);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);


}
