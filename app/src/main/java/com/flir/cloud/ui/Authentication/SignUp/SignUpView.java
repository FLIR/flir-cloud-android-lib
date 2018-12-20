package com.flir.cloud.ui.Authentication.SignUp;

import com.flir.sdk.models.authenticationModel.SignUpResponse;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface SignUpView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void onSuccess();

    void getSignUpResponseState(SignUpResponse signUpResponse);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);
}
