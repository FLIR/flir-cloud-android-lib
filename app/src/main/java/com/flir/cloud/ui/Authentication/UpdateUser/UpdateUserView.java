package com.flir.cloud.ui.Authentication.UpdateUser;

import com.flir.sdk.models.userModel.UpdateUserResponse;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface UpdateUserView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void getUpdateUserResponseState(UpdateUserResponse response);

    void updateUserProfile(UpdateUserResponse updateUserResponse);

}
