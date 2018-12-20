package com.flir.sdk;

import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.network.AuthenticationProvider;

/**
 * Created by Moti Amar on 19/03/2017.
 */

public class ShareElements implements AuthenticationProvider {
    private String accessToken;
    private String typeAs;
    private int expiration;
    private String refreshToken;
    private String accountToken;
    private String accountId;
    public ShareElements( String accessToken, String typeAs, int expiration, String refreshToken,String accountToken, String accountId) {
        this.accessToken = accessToken;
        this.typeAs = typeAs;
        this.expiration = expiration;
        this.refreshToken = refreshToken;
        this.accountToken = accountToken;
        this.accountId = accountId;
    }

    @Override
    public String accessToken() {
        return typeAs +" "+ accessToken;
    }

    @Override
    public boolean hasRefreshToken() {
        return refreshToken != null;
    }


    @Override
    public void updateStorageAuthentication(LoginResponse loginResponse) {

    }

    @Override
    public void updateStorageAuthentication(RefreshTokenResponse refreshTokenResponse) {

    }

    @Override
    public void updateStorageAccountTokenAndID(String accountToken, String accountID) {

    }

    @Override
    public String getAccountToken() {
        return accountToken;
    }

    @Override
    public String getAccountID() {
        return accountId;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public long getExpirationTime() {
        return expiration;
    }

    public void setAccountToken(String aAccountToken){
        accountToken = aAccountToken;
    }

    public void setRefreshToken(String aRefreshToken){
        refreshToken = aRefreshToken;
    }
}
