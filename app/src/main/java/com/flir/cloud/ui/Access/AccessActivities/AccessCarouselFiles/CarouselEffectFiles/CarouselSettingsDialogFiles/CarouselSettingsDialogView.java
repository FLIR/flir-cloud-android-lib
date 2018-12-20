package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.SharedPrefGroupKeyEnum;
import com.flir.cloud.SharedPreferences.SharedPrefValueEnum;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.UpdateDeviceActivity.UpdateDeviceActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.UpdateState;
import com.flir.sdk.network.AuthenticationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.inject.Inject;

/**
 * Created by Moti on 05-Jun-17.
 */

public class CarouselSettingsDialogView extends LinearLayout implements CarouselSettingsView, SharedPreferences.OnSharedPreferenceChangeListener, AdapterView.OnItemSelectedListener {

    public static final String DEVICE_DETAILS_NAME = "deviceDetailsName";
    public static final String DEVICE_DETAILS_SERIAL = "deviceDetailsSerial";

    public static final String DEVICE_DEFAULT_TIME_ZONE = "NONE";

    @Inject
    DeviceInterceptor deviceInterceptor;

    @Inject
    AuthenticationProvider authenticationProvider;

    private Context mContext;
    private LambdaSharedPreferenceManager lambdaSharedPreferenceManager;
    private LambdaCustomProgressBar lambdaCustomProgressBar;
    private TextView recordingModeText;
    private Switch switchRecordingMode;
    private Spinner spinnerResolution;
    private Spinner spinnerTimeZone;
    private LinearLayout updateDeviceLinearLayout;
    private ImageButton updateDeviceDetailsButton;
    private String deviceSerial;
    private String accountId;
    private String deviceName;
    private String channelName;
    private String[] spinnerResolutionList;
    private Vector<String> spinnerTimeZoneVector;
    private CarouselSettingsPresenter carouselSettingsPresenter;
    private boolean isResolutionFirstSelection = true;
    private boolean isTimeZoneFirstSelection = true;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    public CarouselSettingsDialogView(Context context, String serial, String name, String accountId) {
        super(context);
        mContext = context;
        ((MainApplication)mContext.getApplicationContext()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
        deviceSerial = serial;
        deviceName = name;
        this.accountId = accountId;
        carouselSettingsPresenter = new CarouselSettingsPresenter(deviceInterceptor,this);
        channelName = LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + deviceSerial, "");
        initView();
    }

    public CarouselSettingsDialogView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public CarouselSettingsDialogView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView();
    }

    public void initView() {
        lambdaSharedPreferenceManager = LambdaSharedPreferenceManager.getInstance();
        lambdaSharedPreferenceManager.getLambdaSharedPreference().registerOnSharedPreferenceChangeListener(this);

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View dialogView = inflater.inflate(R.layout.devices_carousel_settings_view, null);
        lambdaCustomProgressBar = (LambdaCustomProgressBar) dialogView.findViewById(R.id.lambda_custom_progress_bar_settings_dialog);
        recordingModeText = (TextView) dialogView.findViewById(R.id.tv_recording_mode);
        switchRecordingMode = (Switch) dialogView.findViewById(R.id.switch_recording_mode);
        switchRecordingMode.setChecked(Boolean.valueOf(lambdaSharedPreferenceManager.getStateReporterBySerialAndKey(deviceSerial, channelName, SharedPrefGroupKeyEnum.SD_CARD_DEVICE_RECORDING_MODE, SharedPrefValueEnum.STATE_REPORTED_VALUE_KEY, "false")));
        spinnerResolution = (Spinner) dialogView.findViewById(R.id.spinner_resolution_chooser);
        spinnerTimeZone = (Spinner) dialogView.findViewById(R.id.spinner_change_time_zone);
        updateDeviceLinearLayout = (LinearLayout) dialogView.findViewById(R.id.ll_update_device);
        updateDeviceDetailsButton = (ImageButton) dialogView.findViewById(R.id.btn_update_device_details);
        updateDeviceDetailsButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                openDeviceDetailsPage();
            }
        });

        initSpinnerValues();
        spinnerResolution.setSelection(getSelectionSpinnerPosition());
        initListenersView();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        dialogView.setLayoutParams(layoutParams);
        setUpdateDetailsVisibility();
        addView(dialogView);

    }

    private void setUpdateDetailsVisibility() {
        if(!accountId.equals(authenticationProvider.getAccountID())){
            updateDeviceLinearLayout.setVisibility(GONE);
        }
    }

    private void openDeviceDetailsPage() {
        Intent intent = new Intent(mContext, UpdateDeviceActivity.class);
        intent.putExtra(DEVICE_DETAILS_NAME, deviceName);
        intent.putExtra(DEVICE_DETAILS_SERIAL, deviceSerial);
        mContext.startActivity(intent);
    }

    private void initSpinnerValues() {
        spinnerResolutionList = getResources().getStringArray(R.array.spinner_resolution_list);
        ArrayAdapter aa = new ArrayAdapter(getContext(),android.R.layout.simple_spinner_item,spinnerResolutionList);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerResolution.setAdapter(aa);
        spinnerResolution.setOnItemSelectedListener(this);


        spinnerTimeZoneVector = new Vector<String>(Arrays.asList(getResources().getStringArray(R.array.spinner_time_zone_list)));
        ArrayAdapter timeZone = new ArrayAdapter(mContext,android.R.layout.simple_spinner_item,spinnerTimeZoneVector);
        timeZone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTimeZone.setAdapter(timeZone);
        spinnerTimeZone.setSelection(spinnerTimeZoneVector.indexOf(lambdaSharedPreferenceManager.getLambdaPrefsValue(deviceSerial + LambdaSharedPreferenceManager.DEVICE_SETTINGS_TIME_ZONE ,DEVICE_DEFAULT_TIME_ZONE)));
        spinnerTimeZone.setOnItemSelectedListener(this);
    }


    private void initListenersView() {
        setSwitchRecordingModeListener();
    }

    private void setSwitchRecordingModeListener() {
        switchRecordingMode.setOnClickListener(v -> {
            if(switchRecordingMode.isChecked()){
                recordingModeText.setText(R.string.switch_button_stop_recording);
            }else{
                recordingModeText.setText(R.string.switch_button_start_recording);
            }

            lambdaSharedPreferenceManager.setStateBySerialAndKey(deviceSerial, channelName, SharedPrefGroupKeyEnum.SD_CARD_DEVICE_RECORDING_MODE,
                    SharedPrefValueEnum.STATE_DESIRED_VALUE_KEY, String.valueOf(switchRecordingMode.isChecked()));
            postSettingsRecordingModeUpdateDesired(switchRecordingMode.isChecked(), channelName);

            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_UPDATE_DESIRED_MADE,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_COMMENT_UPDATE_DESIRED_RECORDING_MODE + "/" + switchRecordingMode.isChecked());

        });
    }


    @Override
    public void showWait() {
        lambdaCustomProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        lambdaCustomProgressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {

    }

    @Override
    public void responseFromServer() {

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      if(key.equals(deviceSerial + channelName + SharedPrefGroupKeyEnum.SD_CARD_DEVICE_RECORDING_MODE.getCode() + SharedPrefValueEnum.STATE_REPORTED_VALUE_KEY.getCode())){
            switchRecordingMode.setChecked(Boolean.valueOf(lambdaSharedPreferenceManager.getStateReporterBySerialAndKey(deviceSerial, channelName, SharedPrefGroupKeyEnum.SD_CARD_DEVICE_RECORDING_MODE, SharedPrefValueEnum.STATE_REPORTED_VALUE_KEY, "false")));
            switchRecordingMode.invalidate();
        }if(key.equals(deviceSerial + channelName + SharedPrefGroupKeyEnum.DEVICE_RESOLUTION_MODE.getCode() + SharedPrefValueEnum.STATE_REPORTED_VALUE_KEY.getCode())){
            spinnerResolution.setSelection(getSelectionSpinnerPosition());
            spinnerResolution.invalidate();
        }
    }

    private void postSettingsRecordingModeUpdateDesired(boolean isChecked, String aChannelName) {
        UpdateState ud = new UpdateState();
        List<UpdateState> updateDesiredList = new ArrayList<>();
        ud.setKey(getResources().getString(R.string.device_key_sd_card_recording));
        ud.setValue(Boolean.toString(isChecked));
        ud.setChannel(aChannelName);
        updateDesiredList.add(ud);
        carouselSettingsPresenter.postUpdateDesired(deviceSerial, updateDesiredList);
    }

    private void postSettingsResolutionModeUpdateDesired(String resolution, String aChannelName) {
        UpdateState ud = new UpdateState();
        List<UpdateState> updateDesiredList = new ArrayList<>();
        ud.setKey(getResources().getString(R.string.device_key_resolution));
        ud.setValue(resolution);
        ud.setChannel(aChannelName);
        updateDesiredList.add(ud);
        carouselSettingsPresenter.postUpdateDesired(deviceSerial, updateDesiredList);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //Resolution Chooser
        if(parent.getId() == R.id.spinner_resolution_chooser) {
            if (isResolutionFirstSelection) {
                isResolutionFirstSelection = false;
            } else {
                lambdaSharedPreferenceManager.setStateBySerialAndKey(deviceSerial, channelName, SharedPrefGroupKeyEnum.DEVICE_RESOLUTION_MODE, SharedPrefValueEnum.STATE_DESIRED_VALUE_KEY, spinnerResolutionList[position]);
                postSettingsResolutionModeUpdateDesired(spinnerResolution.getSelectedItem().toString(), lambdaSharedPreferenceManager.getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + deviceSerial, ""));
                mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_UPDATE_DESIRED_MADE,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_COMMENT_UPDATE_DESIRED_RESOLUTION + "/" +spinnerResolution.getSelectedItem().toString());
            }
        }
        //Time zone chooser
        if(parent.getId() == R.id.spinner_change_time_zone) {
            
            if (isTimeZoneFirstSelection) {
                isTimeZoneFirstSelection = false;
            }else {
                lambdaSharedPreferenceManager.setLambdaPrefsValue(deviceSerial + LambdaSharedPreferenceManager.DEVICE_SETTINGS_TIME_ZONE ,spinnerTimeZoneVector.get(position));
                mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_TIMEZONE_CHANGED,
                        "", spinnerTimeZoneVector.get(position));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public int getSelectionSpinnerPosition() {
        String resolution = lambdaSharedPreferenceManager.getStateReporterBySerialAndKey(deviceSerial, channelName, SharedPrefGroupKeyEnum.DEVICE_RESOLUTION_MODE, SharedPrefValueEnum.STATE_REPORTED_VALUE_KEY, "720p");
        for (int i = 0; i < spinnerResolutionList.length; i++) {
            if(spinnerResolutionList[i].equals(resolution)){
                return i;
            }
        }
        return 0;
    }


    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }
}
