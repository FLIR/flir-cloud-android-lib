package com.flir.sdk.Interceptors;

import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.AuthenticationServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

/**
 * Created by Moti Amar on 15/03/2017.
 */

public class AuthInterceptor {
    private AuthenticationServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private final String CONTENT_TYPE = "application/json";

    public AuthInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {

        serviceApi = (AuthenticationServiceApi) serviceGenerator.createService(AuthenticationServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    public Observable putLogout(RefreshToken token) {
        Observable<LogoutResponse> refreshToken = serviceApi.logoutUser(CONTENT_TYPE, token)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends LogoutResponse>>) Observable::error);
        return refreshToken;
    }
}
