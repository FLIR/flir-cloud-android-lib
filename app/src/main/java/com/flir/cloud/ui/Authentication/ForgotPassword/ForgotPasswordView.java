package com.flir.cloud.ui.Authentication.ForgotPassword;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface ForgotPasswordView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void forgotPasswordFinish();

    void onSuccess();

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);
}
