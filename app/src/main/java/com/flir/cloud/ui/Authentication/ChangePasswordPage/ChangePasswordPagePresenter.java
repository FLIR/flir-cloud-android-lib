package com.flir.cloud.ui.Authentication.ChangePasswordPage;

import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.userModel.ChangePassword;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class ChangePasswordPagePresenter {
    private final ChangePasswordPageView view;
    private CompositeDisposable subscriptions;
    private UserInterceptor userInterceptor;


    public ChangePasswordPagePresenter(UserInterceptor userInterceptor, ChangePasswordPageView view) {
        this.view = view;
        this.userInterceptor = userInterceptor;
        subscriptions = new CompositeDisposable();
    }

    public void postChangePasswordRequest(ChangePassword changePassword) {
        Completable completable = userInterceptor.putChangePassword(changePassword);
        completable.subscribe(() -> {
            view.ChangePasswordSucceed();
            view.removeWait();
        }, exception -> {

            view.removeWait();
            view.onFailure();
        });
        view.showWait();
    }

    public void onStop() {
        subscriptions.dispose();
    }
}
