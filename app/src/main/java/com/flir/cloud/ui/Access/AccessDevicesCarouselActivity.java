package com.flir.cloud.ui.Access;

/**
 * Created by Moti on 16-May-17.
 */
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselPagerAdapter;
import com.flir.cloud.ui.Authentication.Login.LoginActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.Interceptors.AuthInterceptor;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.SharedUsersResponse;
import com.flir.sdk.models.Device.DeviceDetails;
import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.network.AuthenticationProvider;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

public class AccessDevicesCarouselActivity extends AppCompatActivity implements AccessView {

    public final static int LOOPS = 1000;
    public CarouselPagerAdapter adapter;
    public static int ItemCount;
    public static String ADD_DEVICE_PAGE_NAME = "unShareResource";
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    /**
     * You shouldn't define first page = 0.
     * Let define firstpage = 'number viewpager size' to make endless carousel
     */
    public static int FIRST_PAGE;

    AccessPresenter accessPresenter;

    @Inject
    AuthenticationProvider authenticationProvider;

    @Inject
    AuthInterceptor authInteractor;

    @Inject
    AccessInterceptor accessInterceptor;

    @Inject
    DeviceInterceptor deviceInterceptor;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        showDialogMassage();
    }

    @BindView(R.id.vp_access_devices_carousel_view)
    public ViewPager pager;

    @BindView(R.id.lambda_custom_progress_bar_acl)
    LambdaCustomProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.access_main_page);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        ButterKnife.bind(this);
        accessPresenter = new AccessPresenter(accessInterceptor, deviceInterceptor, this);
        accessPresenter.postGetAllDevices();

    }



    private void initDevicesPager(List<DeviceDetails> response) {
     //   addDevicePlusItem(response);
        //TODO: Remove the following method when Hadar fix the API
        //ItemCount = getDeviceCount(response);
        ItemCount = response.size();
        //set page margin between pages for viewpager
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int pageMargin = (int) ((metrics.widthPixels / 4) * 1.5);
        pager.setPageMargin(-pageMargin);
        adapter = new CarouselPagerAdapter(this, getSupportFragmentManager(),response);
        pager.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        pager.addOnPageChangeListener(adapter);

        // Set current item to the middle page so we can fling to both
        // directions left and right
        FIRST_PAGE = (ItemCount -1) / 2;
        pager.setCurrentItem(FIRST_PAGE);
        pager.setOffscreenPageLimit(3);
    }

    private void addDevicePlusItem(List<DeviceDetails> response) {
        DeviceDetails plus = new DeviceDetails();
        plus.name = ADD_DEVICE_PAGE_NAME;
        response.add(plus);
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

    }

    @Override
    public void responseFromServer() {

    }

    @Override
    public void responseFromServer(ResourceResponse response) {

    }

    @Override
    public void responseFromServer(List<DeviceDetails> response) {
        LambdaSharedPreferenceManager.getInstance().saveResourcesList(response);
        initDevicesPager(response);
    }


    @Override
    public void responseListFromServer(List<SharedUsersResponse> response) {

    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
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

    private void doLogOut() {
        RefreshToken refreshToken = new RefreshToken(authenticationProvider.getRefreshToken());
        Observable observable = authInteractor.putLogout(refreshToken);
        observable.subscribeWith(new DisposableObserver<LogoutResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(LogoutResponse logoutResponse) {
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

}