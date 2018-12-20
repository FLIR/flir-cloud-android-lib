package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessDevicesCarouselActivity;
import com.flir.cloud.ui.Access.AccessActivities.AccessAddDevice;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraItemActivity;
import com.flir.cloud.ui.Device.DevicePresenter;
import com.flir.cloud.ui.Device.DeviceView;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.DeviceDetails;
import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.models.Device.UploadDownloadUrlResponse;

import java.util.List;

import javax.inject.Inject;

import okhttp3.ResponseBody;

public class CarouselItemFragment extends Fragment implements DeviceView {

    public static final int ADD_DEVICE_RESULT_CODE = 666;
    public static final int DELETE_DEVICE_RESULT_CODE = 766;

    public static final String THUMBNAIL_LAST_TIME_UPDATING = "thumbnailLastTimeUpdating";
    public static final String THUMBNAIL_URL = "thumbnailUrl";
    public static final long THUMBNAIL_LAST_TIME_UPDATING_INTERVAL = 60000;

    DevicePresenter devicePresenter;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @Inject
    DeviceInterceptor deviceInterceptor;

    private LambdaSharedPreferenceManager lambdaSharedPreference;

    private static final String POSITION = "position";
    private static final String SCALE = "scale";
    public static final String NAME = "name";
    public static final String ID = "id";
    public static final String SERIAL = "serial";
    public static final String ACCOUNT_ID = "account";
    public static final String DRAWABLE_THUMBNAIL_RESOURCE = "resource";

    private int screenWidth;
    private int screenHeight;

    private TextView textView;
    private int position;
    private CarouselLinearLayout root;
    private ImageView imageView;
    private String thumbnailUrl;
    private String serial;
    private String id;
    private String name;
    private String accountID;


    public static Fragment newInstance(AccessDevicesCarouselActivity context, int pos, float scale, DeviceDetails deviceResponse) {
        Bundle b = new Bundle();
        b.putInt(POSITION, pos);
        b.putFloat(SCALE, scale);
        b.putString(NAME, deviceResponse.name);
        b.putString(NAME, deviceResponse.name);
        b.putString(ID, deviceResponse.id);
        b.putString(SERIAL, deviceResponse.serial);
        b.putString(ACCOUNT_ID, deviceResponse.account);
        return Fragment.instantiate(context, CarouselItemFragment.class.getName(), b);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWidthAndHeight();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container == null) {
            return null;
        }

        ((MainApplication) getActivity().getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(getContext());
        devicePresenter = new DevicePresenter(deviceInterceptor, this);
        lambdaSharedPreference = LambdaSharedPreferenceManager.getInstance();
        position = this.getArguments().getInt(POSITION);
        float scale = this.getArguments().getFloat(SCALE);
        name = this.getArguments().getString(NAME);
        id = this.getArguments().getString(ID);
        serial = this.getArguments().getString(SERIAL);
        accountID = this.getArguments().getString(ACCOUNT_ID);

        LinearLayout linearLayout;
        if(name != null && name.equals(AccessDevicesCarouselActivity.ADD_DEVICE_PAGE_NAME)){
            linearLayout = initAddDeviceElements(inflater, container);
        }else {
            linearLayout = initElements(inflater, container, name);
            downloadThumbnailImage(serial);
        }

        root.setScaleBoth(scale);

        return linearLayout;
    }

    private LinearLayout initAddDeviceElements(LayoutInflater inflater, ViewGroup container) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (screenWidth * 0.7), (int) (screenHeight * 0.32));
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_caroucel_item_view, container, false);

        textView = (TextView) linearLayout.findViewById(R.id.tv_carousel_item_text);
        root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container);
        imageView = (ImageView) linearLayout.findViewById(R.id.carousel_item_image_view);

        textView.setText(getResources().getString(R.string.carousel_add_device_item));
        textView.setTextColor(Color.WHITE);

        imageView.setLayoutParams(layoutParams);
        imageView.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.ic_icon_plus));
        //handling click event
        imageView.setOnClickListener(v -> {
            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_DEVICE_SELECTION_CAROUSEL_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ADD_DEVICE_ITEM_SELECTED,
                    "", "");

            Intent intent = new Intent(getActivity(), AccessAddDevice.class);
            startActivityForResult(intent, ADD_DEVICE_RESULT_CODE);
        });
        return linearLayout;
    }


    private LinearLayout initElements(LayoutInflater inflater, ViewGroup container, String name) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (screenWidth * 0.65), (int) (screenHeight * 0.27));
        LinearLayout linearLayout = (LinearLayout) inflater.inflate(R.layout.fragment_caroucel_item_view, container, false);

        textView = (TextView) linearLayout.findViewById(R.id.tv_carousel_item_text);
        root = (CarouselLinearLayout) linearLayout.findViewById(R.id.root_container);
        imageView = (ImageView) linearLayout.findViewById(R.id.carousel_item_image_view);


        textView.setText("Camera Name:  " + name + " ");
        textView.setTextColor(ContextCompat.getColor(getActivity() , R.color.carousel_page_text_color));
        imageView.setLayoutParams(layoutParams);

        //handling click event
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CameraItemActivity.class);
            intent.putExtra(DRAWABLE_THUMBNAIL_RESOURCE, thumbnailUrl);
            intent.putExtra(ID, id);
            intent.putExtra(SERIAL, serial);
            intent.putExtra(NAME, name);
            intent.putExtra(ACCOUNT_ID, accountID);
            startActivityForResult(intent, DELETE_DEVICE_RESULT_CODE);

            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_DEVICE_SELECTION_CAROUSEL_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_DEVICE_ITEM_SELECTED,
                    "", serial +"/" + name);


        });
        return linearLayout;
    }


    private void downloadThumbnailImage(String serial) {
        if(checkIfNeedToGetNewUrlForThumbnail()) {
            devicePresenter.getDownloadFile(serial, LambdaConstant.ACCESS_ATTRIBUTE_TYPE_THUMBNAIL);
        }else{
            updateThumbnailPic();
        }
    }

    private boolean checkIfNeedToGetNewUrlForThumbnail() {
        long lastTime = lambdaSharedPreference.getLambdaPrefsValue(getLastTimeUpdatingPrefsKey(), (long)0);
        return lastTime + THUMBNAIL_LAST_TIME_UPDATING_INTERVAL < System.currentTimeMillis();
    }

    private void saveThumbnailLastTimeUpdating(String url) {
        lambdaSharedPreference.setLambdaPrefsValue(getLastTimeUpdatingPrefsKey(),System.currentTimeMillis());
        lambdaSharedPreference.setLambdaPrefsValue(getThumbnailUrlPrefsKey(),url);
    }

    private String getLastTimeUpdatingPrefsKey() {
        return serial + "/" + THUMBNAIL_LAST_TIME_UPDATING;
    }

    private String getThumbnailUrlPrefsKey() {
        return serial + "/" + THUMBNAIL_URL;
    }

    /**
     * Get device screen width and height
     */
    private void getWidthAndHeight() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        screenHeight = displaymetrics.heightPixels;
        screenWidth = displaymetrics.widthPixels;
    }

    @Override
    public void showWait() {

    }

    @Override
    public void removeWait() {

    }

    @Override
    public void onFailure(String appErrorMessage) {

    }

    @Override
    public void responseFromServer() {

    }

    @Override
    public void responseDeviceStateListFromServer(List<GetDeviceStateResponse> responses) {
        for(int i = 0 ; i< responses.size(); i++){
            lambdaSharedPreference.setStateBySerialAndKey(responses.get(i));
        }
    }

    @Override
    public void responseFromServer(UploadDownloadUrlResponse responses) {
        updateThumbnailPic(responses);
    }

    @Override
    public void responseFromServer(ResponseBody body) {

    }

    private void updateThumbnailPic(UploadDownloadUrlResponse responses) {
        saveThumbnailLastTimeUpdating(responses.url);
        thumbnailUrl = responses.url;
        if(getContext() != null) {
            loadImageWithGlide();
        }
    }

    private void updateThumbnailPic() {
        thumbnailUrl = lambdaSharedPreference.getLambdaPrefsValue(getThumbnailUrlPrefsKey(),"");
        if(getContext() != null) {
            loadImageWithGlide();
        }
    }

    private void loadImageWithGlide() {

        RequestOptions requestOptions = new RequestOptions();
        requestOptions.diskCacheStrategy(DiskCacheStrategy.ALL);


        Glide.with(CarouselItemFragment.this)
                .load(thumbnailUrl)
                .apply(requestOptions)
                .into(imageView);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == ADD_DEVICE_RESULT_CODE){
           getActivity().finish();
           startActivity(getActivity().getIntent());
        }
        if(resultCode == DELETE_DEVICE_RESULT_CODE){
           getActivity().finish();
           startActivity(getActivity().getIntent());
        }
    }
}
