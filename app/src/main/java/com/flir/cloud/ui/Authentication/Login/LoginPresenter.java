package com.flir.cloud.ui.Authentication.Login;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.models.authenticationModel.Login;
import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.network.AuthenticationProvider;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class LoginPresenter {
    private final LoginView view;
    private CompositeDisposable subscriptions;
    private LoginInterceptor loginInteractor;
    private AuthenticationProvider authenticationToken;


    public LoginPresenter(LoginInterceptor loginInteractor, LoginView view, AuthenticationProvider authenticationToken) {
        this.view = view;
        this.loginInteractor = loginInteractor;
        this.authenticationToken = authenticationToken;
        subscriptions = new CompositeDisposable();
    }

    public void postLoginRequest(Login login) {
        Observable<LoginResponse> observable= loginInteractor.postLoginRequest(login);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<LoginResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(LoginResponse loginResponse) {
                authenticationToken.updateStorageAuthentication(loginResponse);
                view.loginToServer();
                view.removeWait();
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_LOGIN_BUTTON_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, login.username + "/" + login.password);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_LOGIN_BUTTON_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, login.username + "/" + login.password + "  Error Message = " + e.getMessage());
            }

        }));

        view.showWait();
    }

    public void isRefreshTokenExist(){
        if (authenticationToken.hasRefreshToken()) {
            RefreshToken refreshToken = new RefreshToken(authenticationToken.getRefreshToken());
            loginInteractor.putRefreshToken(refreshToken).subscribeWith(new DisposableObserver<RefreshTokenResponse>() {
                @Override
                public void onNext(RefreshTokenResponse refreshTokenResponse) {
                    authenticationToken.updateStorageAuthentication(refreshTokenResponse);
                    view.loginToServer();
                    view.removeWait();
                }

                @Override
                public void onError(Throwable e) {
                    view.onFailure(e.getMessage());
                    view.hideSplashScreen();
                    view.setEditTextFocus();
                    view.removeWait();
                }

                @Override
                public void onComplete() {

                }
            });
        }else{
            view.hideSplashScreen();
            view.setEditTextFocus();
        }
    }

    public void onStop() {
        subscriptions.dispose();
    }
}
