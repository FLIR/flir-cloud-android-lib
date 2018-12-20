package com.flir.sdk.network;

import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;

/**
 * Created by Moti Amar on 16/03/2017.
 */

public interface AuthenticationProvider {
    String accessToken();
    boolean hasRefreshToken();
    void updateStorageAuthentication(LoginResponse loginResponse);
    void updateStorageAuthentication(RefreshTokenResponse refreshTokenResponse);
    void updateStorageAccountTokenAndID(String accountToken, String accountId);
    String getAccountToken();
    String getAccountID();
    String getRefreshToken();
    long getExpirationTime();
}
