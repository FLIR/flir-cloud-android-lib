package com.flir.cloud.ui.Authentication.SignUp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.authenticationModel.SignUpResponse;
import com.flir.sdk.network.AuthenticationProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class SignUpActivity extends AppCompatActivity implements SignUpView {

    public static final int PASSWORD_MIN_CHARACTERS = 8;
    @Inject
    LoginInterceptor loginInteractor;

    @Inject
    AuthenticationProvider authenticationProvider;

    SignUpPresenter presenter;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @BindView(R.id.lambda_custom_progress_bar_sign_up)
    LambdaCustomProgressBar progressBar;

    @BindView(R.id.input_first_name)
    EditText firstNameEditText;

    @BindView(R.id.input_last_name)
    EditText lasttNameEditText;

    @BindView(R.id.input_email)
    EditText emailEditText;

    @BindView(R.id.input_password)
    EditText passwordEditText;

    @BindView(R.id.input_phone)
    EditText phoneEditText;

    @BindView(R.id.button_signup)
    Button button_signup;

    @OnClick(R.id.button_signup)
    public void SendSignUpRequest(){
        SignUp signUp = new SignUp();
        String first;
        String last;
        String email;
        String password = passwordEditText.getText().toString();
        if(password.length() >= PASSWORD_MIN_CHARACTERS) {
            first = firstNameEditText.getText().toString();
            last = lasttNameEditText.getText().toString();
            email = emailEditText.getText().toString();

            signUp.setFirstName(first);
            signUp.setLastName(last);
            signUp.setEmail(email);
            signUp.setPassword(password);

            presenter.postSignUpRequest(signUp);

            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SIGN_UP_BUTTON_CLICKED,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, email + "/" + password);
        }else {
            Toast.makeText(getApplicationContext(),getApplicationContext().getResources().getString(R.string.password_too_short) , Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        ButterKnife.bind(this);
        presenter = new SignUpPresenter(loginInteractor, this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onStop();
    }

    @Override
    public void showWait() {
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
            progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        //Toast.makeText(getApplicationContext(),"Failure: "+appErrorMessage , Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LambdaConstant.SHOW_SPLASH_FLAG, false);
        startActivity(intent);
    }

    @Override
    public void getSignUpResponseState(SignUpResponse signUpResponse) {
        //Toast.makeText(getApplicationContext(),"Success: "+signUpResponse.id , Toast.LENGTH_LONG).show();
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

}
