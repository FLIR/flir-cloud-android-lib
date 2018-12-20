package com.flir.cloud.ui.Authentication.ChangePasswordPage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Authentication.Login.LoginActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.userModel.ChangePassword;
import com.flir.sdk.network.AuthenticationProvider;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChangePasswordActivity extends AppCompatActivity implements ChangePasswordPageView {

    @Inject
    UserInterceptor userInterceptor;

    ChangePasswordPagePresenter presenter;

    @Inject
    AuthenticationProvider authenticationProvider;

    @BindView(R.id.input_old_password)
    EditText oldPasswordEditText;

    @BindView(R.id.input_new_password)
    EditText newPasswordEditText;

    @BindView(R.id.input_confirm_password)
    EditText confirmPasswordEditText;

    @BindView(R.id.button_change_password)
    Button buttonChangePassword;

    @BindView(R.id.lambda_custom_progress_bar_change_password)
    LambdaCustomProgressBar progressBar;

    private Context mContext;

    @OnClick(R.id.button_change_password)
    public void SendChangePasswordRequest() {
        String oldPassword = oldPasswordEditText.getText().toString();
        String newPassword = newPasswordEditText.getText().toString();
        ChangePassword changePassword = new ChangePassword(oldPassword, newPassword);
        presenter.postChangePasswordRequest(changePassword);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password_page);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        ButterKnife.bind(this);
        mContext = this;
        buttonChangePassword.setEnabled(false);
        presenter = new ChangePasswordPagePresenter(userInterceptor, this);
        initTextChangedListenerToAllEditText();


    }

    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            String oldPassword = oldPasswordEditText.getText().toString();
            String newPassword = newPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();
            if(newPassword.equals(confirmPassword) && !oldPassword.isEmpty() && !newPassword.isEmpty() && !confirmPassword.isEmpty()) {
                buttonChangePassword.setEnabled(true);
            }else{
                buttonChangePassword.setEnabled(false);
            }
        }
    };

    private void initTextChangedListenerToAllEditText() {
        oldPasswordEditText.addTextChangedListener(mTextWatcher);
        newPasswordEditText.addTextChangedListener(mTextWatcher);
        confirmPasswordEditText.addTextChangedListener(mTextWatcher);
    }

    @Override
    public void onStop() {
        super.onStop();
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
    public void onFailure() {
        new MaterialDialog.Builder(this).title(R.string.account_change_password)
                .content(R.string.account_change_password_failed_dialog_title)
                .positiveText(R.string.account_change_password_dialog_agree)
                .show();
    }

    @Override
    public void ChangePasswordSucceed() {

        new MaterialDialog.Builder(this).canceledOnTouchOutside(false).onAny(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                deleteAccountTokenIfExist();
                Intent intent = new Intent(mContext, LoginActivity.class);
                intent.putExtra(LambdaConstant.SHOW_SPLASH_FLAG, false);
                startActivity(intent);
            }
        }).title(R.string.account_change_password_succeed_dialog_title)
                .content(R.string.account_change_password_dialog_content)
                .positiveText(R.string.account_change_password_dialog_agree)
                .show();
    }

    private void deleteAccountTokenIfExist() {
        if(!authenticationProvider.getAccountToken().equals("")){
            authenticationProvider.updateStorageAccountTokenAndID("", "");
        }
    }

}
