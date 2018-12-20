package com.flir.cloud.ui.Authentication.SignUp;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.authenticationModel.SignUpResponse;
import com.flir.sdk.network.NetworkError;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class SignUpPresenter {

    private final SignUpView view;
    private CompositeDisposable subscriptions;
    private LoginInterceptor loginInteractor;

    public SignUpPresenter(LoginInterceptor loginInteractor, SignUpView view) {
        this.view = view;
        this.loginInteractor = loginInteractor;
        subscriptions = new CompositeDisposable();
    }

    public void postSignUpRequest(SignUp signUp) {

        view.showWait();
        Observable<SignUpResponse> observable = loginInteractor.postSignUpRequest(signUp);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<SignUpResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(SignUpResponse signUpResponse) {
                view.getSignUpResponseState(signUpResponse);
                view.removeWait();
                view.onSuccess();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SIGN_UP_BUTTON_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, signUp.email + "/" + signUp.password);
            }

            @Override
            public void onError(Throwable e) {
                NetworkError networkError = new NetworkError(e);
                view.onFailure(networkError.getAppErrorMessage());
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SIGN_UP_BUTTON_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, signUp.email + "/" + signUp.password + "  Error Message = " + e.getMessage());
            }

        }));

        view.showWait();
    }

    public void onStop() {
        subscriptions.dispose();
    }
}
