package com.flir.cloud.ui.Account;

import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;

import java.util.List;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public interface AccountView {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void onFailure(String appErrorMessage, String myMessage);

    void responseFromServer(CreateAccountResponse response);

    void responseFromServer(GetAccountResponse response);

    void responseFromServer(List<CreateAccountResponse> response);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);

}
