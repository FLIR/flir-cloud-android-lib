package com.flir.cloud.ui.Authentication.UserProfile;

/**
 * Created by Moti on 24-May-17.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessDevicesCarouselActivity;
import com.flir.cloud.ui.Account.AccountPageActivity;
import com.flir.cloud.ui.Authentication.ChangePasswordPage.ChangePasswordActivity;
import com.flir.cloud.ui.Authentication.Login.LoginActivity;
import com.flir.cloud.ui.Authentication.UpdateUser.UpdateUserActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.cloud.ui.fileExplorer.VolumePageClasses.VolumeMainPageActivity;
import com.flir.sdk.Interceptors.AuthInterceptor;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.models.userModel.UpdateUserResponse;
import com.flir.sdk.network.AuthenticationProvider;

import net.opacapp.multilinecollapsingtoolbar.CollapsingToolbarLayout;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public class UserProfileMainPagePage extends AppCompatActivity implements UserProfileView {

    private static final int RESULT_UPDATE_PROFILE_DETAILS = 111;
    private static final int RESULT_UPDATE_IMAGE_UPDATED = 211;
    UserProfilePresenter userProfilePresenter;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @Inject
    UserInterceptor userInterceptor;

    @Inject
    AuthenticationProvider authenticationProvider;

    @Inject
    AuthInterceptor authInteractor;

    @BindView(R.id.lambda_custom_progress_bar_profile_page)
    LambdaCustomProgressBar progressBar;

    @BindView(R.id.toolbar_main_page)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;

    @BindView(R.id.iv_profile_photo)
    ImageView profileImageView;

    @BindView(R.id.main_page_fab)
    FloatingActionButton main_fab;

    @OnClick(R.id.main_page_fab)
    void onFabClicked() {
        startActivityForResult(new Intent(this, UpdateUserActivity.class), RESULT_UPDATE_PROFILE_DETAILS);
    }

    @OnClick(R.id.ib_my_devices)
    void myDevicesClicked() {
            openAccessDevicesView();
    }

    @OnClick(R.id.ib_add_new_device)
    void myFileExplorerClicked() {
            openFileExplorerView();
    }

    private void openAccessDevicesView() {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_MAIN_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_DEVICES_VIEW_CLICKED, null, null);

        Intent i = new Intent(this, AccessDevicesCarouselActivity.class);
        startActivity(i);
    }

    private void openFileExplorerView() {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_MAIN_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_STORAGE_VIEW_CLICKED, null, null);

        Intent i = new Intent(this, VolumeMainPageActivity.class);
        startActivity(i);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        userProfilePresenter = new UserProfilePresenter(userInterceptor, this);
        initElements();
    }

    private void initElements() {

        userProfilePresenter.getSelfUser();
        toolbar.setTitle("");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.main_page_my_devices));
        setSupportActionBar(toolbar);
    }

    @Override
    public void onBackPressed() {
        showDialogMassage();
    }

    private void showDialogMassage() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    doLogOut();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_massage_massage)).setPositiveButton(getResources().getString(R.string.dialog_massage_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.dialog_massage_no), dialogClickListener).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.log_out:
                // doLogOut();
                showDialogMassage();
                return true;
            case R.id.change_password:
                doChangePassword();
                return true;
            case R.id.change_account:
                doChangeAccount();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void doLogOut() {
        RefreshToken refreshToken = new RefreshToken(authenticationProvider.getRefreshToken());
        Observable observable = authInteractor.putLogout(refreshToken);
        observable.subscribeWith(new DisposableObserver<LogoutResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(LogoutResponse logoutResponse) {
                progressBar.setVisibility(View.GONE);
                logoutSucceeded();
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    System.out.println(httpException.getMessage());
                }
            }
        });
        progressBar.setVisibility(View.VISIBLE);
    }

    private void doChangeAccount() {
        Intent intent = new Intent(this, AccountPageActivity.class);
        intent.putExtra(LambdaConstant.FORCE_CREATE_ACCOUNT, true);
        startActivity(intent);
    }

    private void doChangePassword() {
        Intent intent = new Intent(this, ChangePasswordActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        MenuItem item = menu.findItem(R.id.log_out);
        SpannableString s = new SpannableString(getResources().getString(R.string.log_out));
        s.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.main_page_menu_text_color)), 0, s.length(), 0);
        s.setSpan(new RelativeSizeSpan(0.8f), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        item.setTitle(s);

        MenuItem changePassword = menu.findItem(R.id.change_password);
        SpannableString SpannableChangePasswordString = new SpannableString(getResources().getString(R.string.account_change_password));
        SpannableChangePasswordString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.main_page_menu_text_color)), 0, SpannableChangePasswordString.length(), 0);
        SpannableChangePasswordString.setSpan(new RelativeSizeSpan(0.8f), 0, SpannableChangePasswordString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        changePassword.setTitle(SpannableChangePasswordString);


        MenuItem changeAccount = menu.findItem(R.id.change_account);
        SpannableString SpannableAccountString = new SpannableString(getResources().getString(R.string.account_change_account));
        SpannableAccountString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.main_page_menu_text_color)), 0, SpannableAccountString.length(), 0);
        SpannableAccountString.setSpan(new RelativeSizeSpan(0.8f), 0, SpannableAccountString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        changeAccount.setTitle(SpannableAccountString);

        return true;
    }

    public void logoutSucceeded() {
        deleteAccountTokenIfExist();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LambdaConstant.SHOW_SPLASH_FLAG, false);
        startActivity(intent);
    }

    private void deleteAccountTokenIfExist() {
        if(!authenticationProvider.getAccountToken().equals("")){
            authenticationProvider.updateStorageAccountTokenAndID("", "");
        }
    }

    @Override
    public void updateUserProfile(UpdateUserResponse updateUserResponse) {
        if (updateUserResponse.userInfo != null) {
            updateTitleDetails(updateUserResponse.userInfo.firstName,updateUserResponse.userInfo.lastName, updateUserResponse.userInfo.email);
            updateUserProfilePicture(updateUserResponse.userInfo.pictureUrl);
        }
    }

    @Override
    public void updateUserProfileImage(String imageUrl) {
        updateUserProfileImage(imageUrl);
    }

    private void updateTitleDetails(String firstName, String lastName, String email) {
        toolbarLayout.setTitle(firstName + " " + lastName + " " + email);
    }
    public void updateUserProfilePicture(String pictureUrl){
        Glide.with(this).asBitmap().load(pictureUrl).into(new BitmapImageViewTarget(profileImageView) {
            @Override
            protected void setResource(Bitmap resource) {
                RoundedBitmapDrawable circularBitmapDrawable =
                        RoundedBitmapDrawableFactory.create(getResources(), resource);
                circularBitmapDrawable.setCircular(true);
                profileImageView.setImageDrawable(circularBitmapDrawable);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (resultCode) {
            case RESULT_UPDATE_PROFILE_DETAILS:
                if(data != null) {
                    updateTitleDetails(data.getStringExtra(LambdaConstant.UPDATE_DETAILS_PAGE_PREF_FIRST_NAME),
                            data.getStringExtra(LambdaConstant.UPDATE_DETAILS_PAGE_PREF_LAST_NAME),
                            data.getStringExtra(LambdaConstant.UPDATE_DETAILS_PAGE_PREF_EMAIL));
                }
                break;
            case RESULT_UPDATE_IMAGE_UPDATED:
                if(data != null) {
                    updateUserProfilePicture(data.getStringExtra(LambdaConstant.UPDATE_DETAILS_IMAGE_UPDATED));
                }

                break;
            default:
                break;
        }
    }
}