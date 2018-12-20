package com.flir.cloud.ui.Authentication.ForgotPassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Authentication.Login.LoginActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.userModel.ForgetPassword;


import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ForgotPasswordActivity extends Activity implements ForgotPasswordView {

    @Inject
    UserInterceptor userInterceptor;

    ForgotPasswordPresenter presenter;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @BindView(R.id.lambda_custom_progress_bar_forgot_password)
    LambdaCustomProgressBar progressBarResetPassword;

    @BindView(R.id.et_input_username)
    EditText userNameEditText;

    @BindView(R.id.button_reset_password)
    Button buttonResetPassword;

    @OnClick(R.id.button_reset_password)
    public void SendChangePasswordRequest() {
        String userName = userNameEditText.getText().toString();
        ForgetPassword forgotPassword = new ForgetPassword(userName);
        presenter.postForgotPasswordRequest(forgotPassword);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_RESET_PASSWORD_BUTTON_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, userName);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        presenter = new ForgotPasswordPresenter(userInterceptor, this);
    }

    @Override
    public void onStop() {
        super.onStop();
        presenter.onStop();
    }

    @Override
    public void showWait() {
            progressBarResetPassword.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
            progressBarResetPassword.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        Toast.makeText(this, "Failure: " + appErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void forgotPasswordFinish() {
        //Toast.makeText(this, "finish forgot password", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LambdaConstant.SHOW_SPLASH_FLAG, false);
        startActivity(intent);
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }


}
