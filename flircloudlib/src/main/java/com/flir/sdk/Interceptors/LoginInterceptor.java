package com.flir.sdk.Interceptors;

import com.flir.sdk.models.authenticationModel.Login;
import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.models.authenticationModel.ResendVerificationRequest;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.authenticationModel.SignUpResponse;
import com.flir.sdk.network.ServiceApiType.AuthenticationServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by Moti Amar on 22/03/2017.
 */

public class LoginInterceptor {
    private AuthenticationServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private final String CONTENT_TYPE = "application/json";

    public LoginInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler){
        serviceApi = (AuthenticationServiceApi) serviceGenerator.createService(AuthenticationServiceApi.class);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    public Observable postSignUpRequest(SignUp signUp) {
        Observable<SignUpResponse> signUpToServer = serviceApi.signUpToServer(CONTENT_TYPE,signUp)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends SignUpResponse>>) Observable::error);
        return signUpToServer;
    }

    public Observable ResendVerification(ResendVerificationRequest aResendVerificationRequest) {
        Observable<ResponseBody> signUpToServer = serviceApi.ResendVerification(CONTENT_TYPE, aResendVerificationRequest)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends ResponseBody>>) Observable::error);
        return signUpToServer;
    }

    public Observable<LoginResponse> postLoginRequest(Login login) {
        Observable<LoginResponse> loginToServer = serviceApi.loginToServer(CONTENT_TYPE,login)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends LoginResponse>>) Observable::error);
        return loginToServer;

    }

    public Observable putRefreshToken(RefreshToken token) {
        Observable<RefreshTokenResponse> refreshToken = serviceApi.refreshToken(CONTENT_TYPE,token)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends RefreshTokenResponse>>) Observable::error);

        return refreshToken;
    }
    public Call<RefreshTokenResponse> refreshTokenSync(RefreshToken token) {
        Call<RefreshTokenResponse> refreshToken = serviceApi.refreshTokenSync(CONTENT_TYPE,token);
        return refreshToken;
    }
}
