package com.flir.cloud.ui.Authentication.Login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.BuildConfig;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.Services.EventMqttIntentService;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.LambdaUtils;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessDevicesCarouselActivity;
import com.flir.cloud.ui.Account.AccountPageActivity;
import com.flir.cloud.ui.Authentication.ForgotPassword.ForgotPasswordActivity;
import com.flir.cloud.ui.Authentication.SignUp.SignUpActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.EventsInterceptor;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.models.authenticationModel.Login;
import com.flir.sdk.network.AuthenticationProvider;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class LoginActivity extends AppCompatActivity implements LoginView {

    @Inject
    LoginInterceptor loginInteractor;

    @Inject
    EventsInterceptor eventsInterceptor;

    @Inject
    AuthenticationProvider authenticationToken;

    LoginPresenter presenter;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @BindView(R.id.ll_flir_splash_screen)
    LinearLayout linearLayoutSplashScreen;

    @BindView(R.id.sign_up_text)
    TextView signUpTextView;

    @BindView(R.id.forgot_password)
    TextView forgotPasswordTextView;

    @BindView(R.id.lambda_custom_progress_bar_login)
    LambdaCustomProgressBar lambdaProgressBarLogIn;

    @BindView(R.id.input_username)
    EditText usernameEditText;

    @BindView(R.id.input_password)
    EditText passwordEditText;

    @BindView(R.id.change_base_url)
    ImageButton changeBaseUrl;

    @BindView(R.id.gif_thermal_view_splash_screen)
    public GifImageView mGifView;

    @OnClick(R.id.change_base_url)
    public void changeBaseUrlClicked() {
        doChangeBaseUrlClicked();
    }


    @BindView(R.id.button_login)
    Button button_login;

    private Intent intent;

    @OnClick(R.id.button_login)
    public void SendLoginRequest() {
        Login login = new Login();
        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        login.setUsername(username);
        login.setPassword(password);
        login.setExpirationDuration(LambdaConstant.LOGIN_EXPIRATION_DURATION);

        presenter.postLoginRequest(login);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_LOGIN_BUTTON_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, username + "/" + password);


    }

    @OnClick(R.id.sign_up_text)
    public void goToSignUpPage() {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                                               LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_OPEN_SIGN_UP_ACTIVITY,
                                               "", "");

        startActivity(new Intent(this, SignUpActivity.class));
    }

    @OnClick(R.id.forgot_password)
    public void goToForgotPasswordPage() {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_OPEN_FORGOT_PASSWORD_ACTIVITY,
                "", "");
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        debugModeConfiguration();
        boolean showSplashScreen = getIntent().getBooleanExtra(LambdaConstant.SHOW_SPLASH_FLAG, true);
        presenter = new LoginPresenter(loginInteractor, this, authenticationToken);
        initIntentClass();
        if(showSplashScreen) {
            //Show splash screen before start login view (SplashScreen Visibility = Visible)
            presenter.isRefreshTokenExist();
        }else{
            hideSplashScreen();
        }

        initGif();
    }

    private void initGif() {
        //Init Gif:
        GifDrawable gd = null;
        try {
            gd = new GifDrawable(getResources(), R.drawable.splash_gif);
        } catch (IOException e) {
            e.printStackTrace();
        }
        final GifDrawable finalGd = gd;
        if(gd != null) {
            gd.addAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationCompleted(int loopNumber) {
                    finalGd.reset();
                }
            });
            mGifView.setImageDrawable(gd);
        }
    }

    private void initIntentClass() {
        if(authenticationToken.getAccountToken().isEmpty()) {
            intent = new Intent(this, AccountPageActivity.class);
        }else {
            intent = new Intent(this, AccessDevicesCarouselActivity.class);
        }
    }

    private void debugModeConfiguration() {
        if(BuildConfig.DEBUG){
            changeBaseUrl.setVisibility(View.VISIBLE);
        }else{
            changeBaseUrl.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onStop();
    }

    @Override
    public void showWait() {
        lambdaProgressBarLogIn.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
       // lambdaProgressBarLogIn.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {
        //Toast.makeText(getApplicationContext(), "Failure: " + appErrorMessage, Toast.LENGTH_LONG).show();
    }


    @Override
    public void loginToServer() {
        //Toast.makeText(this, "Success", Toast.LENGTH_SHORT).show();
        startActivity(intent);
        startEventService();
    }

    private void startEventService() {
        if (!LambdaUtils.isMyServiceRunning(EventMqttIntentService.class, this)) {
            Intent i = new Intent(this, EventMqttIntentService.class);
            startService(i);
        }
    }

    @Override
    public void showSplashScreen() {
        linearLayoutSplashScreen.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideSplashScreen() {
        linearLayoutSplashScreen.setVisibility(View.GONE);
    }

    @Override
    public void setEditTextFocus() {
        if(usernameEditText != null && usernameEditText.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }


    private void doChangeBaseUrlClicked() {
        Set<String> deviceListNamesSet = new HashSet<>();
        deviceListNamesSet.add("int");
        deviceListNamesSet.add("uat");
        deviceListNamesSet.add("production");

        new MaterialDialog.Builder(this)
                .title("Change Base Url")
                .items(deviceListNamesSet)
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        String baseUrl = LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(LambdaSharedPreferenceManager.LAMBDA_BASE_URL, "https:/lambda.cloud.flir/");

                        if (text.toString().equals("production")) {
                            if (!baseUrl.contains("https:/lambda")) {
                                LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.LAMBDA_BASE_URL, "https:/lambda.cloud.flir/");
                            }
                            finish();
                            startActivity(getIntent());
                        } else {
                            if (!baseUrl.contains(text.toString())) {
                                LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.LAMBDA_BASE_URL, "https:/" + text.toString() + "-" + "lambda.cloud.flir/");
                                finish();
                                startActivity(getIntent());
                            }
                        }
                    }
                })
                .show();
    }

}
