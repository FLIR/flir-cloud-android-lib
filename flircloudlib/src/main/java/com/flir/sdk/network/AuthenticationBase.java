package com.flir.sdk.network;

import android.content.SharedPreferences;

import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.network.AuthenticationProvider;

import java.util.Date;

/**
 * Created by Moti Amar on 23/03/2017.
 */

//AuthenticationBase created for NetworkModule.
//AuthenticationBase provide everything the system needs include of user connection information etc.

public class AuthenticationBase implements AuthenticationProvider {
    private static final String ACCESS_TOKEN = "access_token";
//    private static final String EXPIRATION = "expiration";
    private static final String REFRESH_TOKEN = "refresh_token";
    private static final String ACCOUNT_TOKEN = "account_token";
    private static final String ACCOUNT_ID = "account_id";

    private SharedPreferences sharedPreferences;
    private String accessToken;
    private long expiration;
    private String refreshToken;

    public AuthenticationBase(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public String getRefreshToken() {
        return sharedPreferences.getString(REFRESH_TOKEN, "");
    }

    @Override
    public long getExpirationTime() {
        return expiration;
    }

    @Override
    public String accessToken() {
      return sharedPreferences.getString(ACCESS_TOKEN, "");
    }

//    private boolean checkExpirationTime() {
//        float currentTime = new Date().getTime() * 1000;
//        float expirationTime = sharedPreferences.getFloat(EXPIRATION, 0);
//        return (expirationTime > currentTime);
//    }

    @Override
    public boolean hasRefreshToken() {
        return sharedPreferences.contains(REFRESH_TOKEN);
    }

    @Override
    public void updateStorageAuthentication(RefreshTokenResponse refreshTokenResponse) {
        long currentTime = new Date().getTime() * 1000;
        expiration = currentTime + refreshTokenResponse.expiration;
        accessToken = refreshTokenResponse.typeAs + " " + refreshTokenResponse.accessToken;

        sharedPreferences.edit().putString(ACCESS_TOKEN, refreshTokenResponse.typeAs + " " + refreshTokenResponse.accessToken).apply();
//        sharedPreferences.edit().putFloat(EXPIRATION, expirationTime).apply();
    }

    @Override
    public void updateStorageAccountTokenAndID(String accountToken, String accountID) {
        sharedPreferences.edit().putString(ACCOUNT_TOKEN, accountToken).apply();
        sharedPreferences.edit().putString(ACCOUNT_ID, accountID).apply();
    }

    @Override
    public String getAccountToken() {
        return sharedPreferences.getString(ACCOUNT_TOKEN, "");
    }

    @Override
    public String getAccountID() {
        return sharedPreferences.getString(ACCOUNT_ID, "");
    }

    @Override
    public void updateStorageAuthentication(LoginResponse loginResponse) {
        long currentTime = new Date().getTime() * 1000;
//        float expirationTime = currentTime + loginResponse.expiration;
        expiration = currentTime + loginResponse.expiration;
        accessToken = loginResponse.typeAs + " " + loginResponse.accessToken;
        sharedPreferences.edit().putString(ACCESS_TOKEN, loginResponse.typeAs + " " + loginResponse.accessToken).apply();
//        sharedPreferences.edit().putFloat(EXPIRATION, expirationTime).apply();
        sharedPreferences.edit().putString(REFRESH_TOKEN, loginResponse.refreshToken).apply();
    }
}
