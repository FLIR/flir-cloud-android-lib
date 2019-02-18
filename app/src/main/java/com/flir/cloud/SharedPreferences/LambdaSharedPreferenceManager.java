package com.flir.cloud.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.flir.cloud.MainApplication;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Device.DeviceDetails;
import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.models.Storage.VolumeResponse;

import java.util.List;


/**
 * Created by Moti on 05-Jun-17.
 */

public class LambdaSharedPreferenceManager {


    public static final String DEVICES_LIST_IDS = "DevicesListIds";
    public static final String VOLUMES_LIST_IDS = "VolumesListIds";

    public static final String MQTT_URL_CONNECTION = "mqttUrlConnection";

    public static final String LAMBDA_BASE_URL = "lambdaBaseUrl";

    //====================== Const================
    //state key values
    public static final String SD_CARD_DEVICE_RECORDING_MODE = "sdCardRecording";
    public static final String DEVICE_RESOLUTION_MODE = "Resolution";

    public static final String MY_PREFS_NAME = "Lambda_Prefs_Key";

    public static final String DEVICE_SETTINGS_TIME_ZONE = "timeZone";

    public static final String SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT = "DisplayOptionSpanCount";

    public static final String SHARED_PREFERENCE_STATE_ITEM_SAVED_NAME = "savedItemNameState";

    public static final String SHARED_PREFERENCE_SELECTED_CHANNEL = "SelectedChannel";
    public static final String SHARED_PREFERENCE_SUPPORTED_CHANNEL = "supportedStreams";

    public static final String SHARED_PREFERENCE_REPORTED_VALUE = "reportedValue";
    public static final String SHARED_PREFERENCE_DESIRED_VALUE = "desiredValue";

    public static final String SHARED_PREFERENCE_UPLOAD_URL = "videoUploadUrl";
    //================================================

    private SharedPreferences lambdaSharedPreferences;

    private static LambdaSharedPreferenceManager INSTANCE;

    private static void init() {
        if (INSTANCE == null) {
            INSTANCE = new LambdaSharedPreferenceManager();
        }
    }

    public static LambdaSharedPreferenceManager getInstance() {
        if(INSTANCE == null){
            init();
        }
        return INSTANCE;
    }

    private LambdaSharedPreferenceManager() {
        lambdaSharedPreferences = MainApplication.getAppContext().getSharedPreferences(MY_PREFS_NAME, Context.MODE_PRIVATE);
    }

    public SharedPreferences getLambdaSharedPreference(){
        return lambdaSharedPreferences;
    }

    public void setLambdaPrefsValue(String key, String value){
        SharedPreferences.Editor editor =  lambdaSharedPreferences.edit();
        editor.putString(key,value);
        editor.apply();
    }
    public void setLambdaPrefsValue(String key, int value){
        SharedPreferences.Editor editor =  lambdaSharedPreferences.edit();
        editor.putInt(key,value);
        editor.apply();
    }
    public void setLambdaPrefsValue(String key, Boolean value){
        SharedPreferences.Editor editor =  lambdaSharedPreferences.edit();
        editor.putBoolean(key,value);
        editor.apply();
    }

    public void setLambdaPrefsValue(String key, long value){
        SharedPreferences.Editor editor =  lambdaSharedPreferences.edit();
        editor.putLong(key,value);
        editor.apply();
    }

    public String getLambdaPrefsValue(String key, String def){
        return lambdaSharedPreferences.getString(key, def);
    }

    public int getLambdaPrefsValue(String key, int def){
        return lambdaSharedPreferences.getInt(key, def);
    }

    public boolean getLambdaPrefsValue(String key, boolean def){
        return lambdaSharedPreferences.getBoolean(key, def);
    }

    public long getLambdaPrefsValue(String key, long def){
        return lambdaSharedPreferences.getLong(key, def);
    }

    public String getStateReporterBySerialAndKey(String serial, String channelName ,SharedPrefGroupKeyEnum sharedPrefGroupKeyEnum, SharedPrefValueEnum valueEnum, String def) {
        String generatedValue = serial + channelName + sharedPrefGroupKeyEnum.getCode() + valueEnum.getCode();
        return getLambdaPrefsValue(generatedValue, def);
    }

    public int getStateReporterBySerialAndKey(String serial, String channelName ,SharedPrefGroupKeyEnum sharedPrefGroupKeyEnum, SharedPrefValueEnum valueEnum, int def) {
        String generatedValue = serial + channelName + sharedPrefGroupKeyEnum.getCode() + valueEnum.getCode();
        return getLambdaPrefsValue(generatedValue, def);
    }

    public void setStateBySerialAndKey(String serial, String channelName, SharedPrefGroupKeyEnum sharedPrefGroupKeyEnum, SharedPrefValueEnum valueEnum , String value) {
        String generatedValue = serial + channelName + sharedPrefGroupKeyEnum.getCode() + valueEnum.getCode();
        setLambdaPrefsValue(generatedValue, value);
    }

    public void setStateBySerialAndKey(String serial, String channelName, SharedPrefGroupKeyEnum sharedPrefGroupKeyEnum, SharedPrefValueEnum valueEnum, int value) {
        String generatedValue = serial + channelName + sharedPrefGroupKeyEnum.getCode() + valueEnum.getCode();
         setLambdaPrefsValue(generatedValue, value);
    }

    public void setStateBySerialAndKey(GetDeviceStateResponse responseItem) {

        String prefPrefix = responseItem.id + responseItem.channel + responseItem.key;
        if (responseItem.channel != null) {
            setLambdaPrefsValue(prefPrefix + SharedPrefValueEnum.STATE_CHANNEL_KEY.getCode(), responseItem.channel);
        }
        //setLambdaPrefsValue(prefPrefix + SharedPrefValueEnum.STATE_DESIRED_VALUE_KEY.getCode(), responseItem.desiredValue);
        setLambdaPrefsValue(prefPrefix + SharedPrefValueEnum.STATE_REPORTED_VALUE_KEY.getCode(), responseItem.reportedValue);
        setLambdaPrefsValue(prefPrefix + SharedPrefValueEnum.STATE_VERSION_KEY.getCode(), responseItem.version);
        setLambdaPrefsValue(prefPrefix + SharedPrefValueEnum.STATE_UPDATE_TIME_KEY.getCode(), responseItem.updatedTime);
    }

    public void saveResourcesList(List<DeviceDetails> response) {

        String devicesListNames = "";
        for (int i = 0; i < response.size(); i++) {
                devicesListNames += response.get(i).id + ",";
        }

        setLambdaPrefsValue(DEVICES_LIST_IDS, devicesListNames);
    }

    public void addDeviceToListDevices(ResourceResponse response) {

        String devicesListNames = getLambdaPrefsValue(DEVICES_LIST_IDS,"");
        devicesListNames+= response.id + ",";
        setLambdaPrefsValue(DEVICES_LIST_IDS,devicesListNames);
    }

    public String[] getDeviceList() {
        return getLambdaPrefsValue(DEVICES_LIST_IDS,"").split(",");
    }

    public String[] getVolumeList() {
        return getLambdaPrefsValue(VOLUMES_LIST_IDS,"").split(",");
    }

    public void saveVolumesList(List<VolumeResponse> response) {

        String volumesListNames = "";
        for (int i = 0; i < response.size(); i++) {
            volumesListNames+= response.get(i).VolumeId + ",";
        }
        setLambdaPrefsValue(VOLUMES_LIST_IDS, volumesListNames);
    }

    public void addVolumeToVolumeList(VolumeResponse response) {

        String volumesListNames = getLambdaPrefsValue(VOLUMES_LIST_IDS,"");
        volumesListNames+= response.VolumeId + ",";
        setLambdaPrefsValue(VOLUMES_LIST_IDS, volumesListNames);
    }

    public String getSelectedChannel(String serial){
        return getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + serial ,(getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SUPPORTED_CHANNEL + serial,"")).split(",")[0]);
    }

}
