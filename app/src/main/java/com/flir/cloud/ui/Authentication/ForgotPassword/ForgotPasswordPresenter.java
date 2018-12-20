package com.flir.cloud.ui.Authentication.ForgotPassword;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.userModel.ForgetPassword;

import io.reactivex.Completable;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class ForgotPasswordPresenter {
    private final ForgotPasswordView view;
    private CompositeDisposable subscriptions;
    private UserInterceptor userInterceptor;


    public ForgotPasswordPresenter(UserInterceptor userInterceptor, ForgotPasswordView view) {
        this.view = view;
        this.userInterceptor = userInterceptor;
        subscriptions = new CompositeDisposable();
    }

    public void postForgotPasswordRequest(ForgetPassword forgetPassword) {
        Completable completable = userInterceptor.postForgotPassword(forgetPassword);
        completable.subscribe(() -> {
            view.forgotPasswordFinish();
            view.removeWait();
            view.onSuccess();

            view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_RESET_PASSWORD_BUTTON_CLICKED,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, forgetPassword.email);
        }, exception -> {
            view.removeWait();
            view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_RESET_PASSWORD_BUTTON_CLICKED,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, forgetPassword.email  + "  Error Message = " + exception.getMessage());
        });
        view.showWait();
    }


    public void onStop() {
        subscriptions.dispose();
    }
}
