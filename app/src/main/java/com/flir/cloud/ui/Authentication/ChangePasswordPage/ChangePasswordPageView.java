package com.flir.cloud.ui.Authentication.ChangePasswordPage;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface ChangePasswordPageView {
    void showWait();

    void removeWait();

    void onFailure();

    void ChangePasswordSucceed();

}
