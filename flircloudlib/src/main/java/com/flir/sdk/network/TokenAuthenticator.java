package com.flir.sdk.network;


import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.network.ServiceApiType.AuthenticationServiceApi;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import retrofit2.Call;

/**
 * Created by Moti Amar on 28/03/2017.
 */

//TokenAuthenticator was created to refresh account to be online using the RefreshToken as Lambda API demand.
//In any response, a request is made to refresh the connection

public class TokenAuthenticator implements Authenticator {

    private AuthenticationProvider AuthenticationProvider;
    private AuthenticationServiceApi serviceApi;

    public TokenAuthenticator(ServiceGenerator serviceGenerator, AuthenticationProvider authenticationProvider) {
        AuthenticationProvider = authenticationProvider;
        serviceApi = (AuthenticationServiceApi) serviceGenerator.createService(AuthenticationServiceApi.class);
    }

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        RefreshToken refreshToken = new RefreshToken(AuthenticationProvider.getRefreshToken());
        Call<RefreshTokenResponse> refreshTokenCall = serviceApi.refreshTokenSync(Constants.CONTENT_TYPE, refreshToken);
        RefreshTokenResponse refreshTokenResponse = refreshTokenCall.execute().body();
        if (refreshTokenResponse != null) {
            AuthenticationProvider.updateStorageAuthentication(refreshTokenResponse);
            return response.request().newBuilder()
                    .header("Authorization", AuthenticationProvider.accessToken())
                    .build();
        }else if(response.code() == Constants.AUTHENTICATION_HAS_FAILED_CODE){
            System.out.println("Time to refresh token has expired, Please do login again");
            Call<LogoutResponse> logoutUserCall = serviceApi.logoutUserSync(Constants.CONTENT_TYPE, refreshToken);
            logoutUserCall.execute();
        }
        return null;
    }
}
