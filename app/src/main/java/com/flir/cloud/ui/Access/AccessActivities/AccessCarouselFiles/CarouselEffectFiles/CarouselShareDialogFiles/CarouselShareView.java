package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselShareDialogFiles;


import com.flir.sdk.models.Access.SharedUsersResponse;

import java.util.List;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface CarouselShareView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer();

    void responseFromServer(List<SharedUsersResponse> res);

    void responseFromServer(String email, int isShareOrUnShare);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);

}
