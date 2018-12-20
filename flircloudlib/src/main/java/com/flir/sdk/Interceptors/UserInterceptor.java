package com.flir.sdk.Interceptors;

import com.flir.sdk.models.userModel.ChangePassword;
import com.flir.sdk.models.userModel.ForgetPassword;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.userModel.UpdateUserResponse;
import com.flir.sdk.models.userModel.UserPicture;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.AuthenticationServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;
import okhttp3.RequestBody;

/**
 * Created by Moti Amar on 16/03/2017.
 */

public class UserInterceptor {
    private AuthenticationServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private final String CONTENT_TYPE = "application/json";

    public UserInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (AuthenticationServiceApi) serviceGenerator.createService(AuthenticationServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }

    public Completable postForgotPassword(ForgetPassword forgetPassword) {
        return serviceApi.forgotPassword(CONTENT_TYPE, forgetPassword)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable putUpdateUser(SignUp signUp) {
        return serviceApi.updateUser(CONTENT_TYPE, signUp)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends UpdateUserResponse>>) Observable::error);
    }

    public Completable putChangePassword(ChangePassword changePassword) {
        return serviceApi.changePassword(CONTENT_TYPE, changePassword)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable getSelfUser() {
        return serviceApi.getSelfUser(CONTENT_TYPE)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends UpdateUserResponse>>) Observable::error);
    }

    public Completable postUploadUserProfilePicture(RequestBody requestBody) {
        return serviceApi.uploadUserProfilePicture("application/octet-stream",requestBody)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable getUserPicture() {
        return serviceApi.getUserPicture(CONTENT_TYPE)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends UserPicture>>) Observable::error);
    }
}
