package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles;


/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface CarouselSettingsView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer();

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);

}
