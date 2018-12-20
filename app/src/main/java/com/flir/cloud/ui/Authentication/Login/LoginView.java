package com.flir.cloud.ui.Authentication.Login;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface LoginView {
    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void loginToServer();

    void showSplashScreen();

    void hideSplashScreen();

    void setEditTextFocus();

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);
}
