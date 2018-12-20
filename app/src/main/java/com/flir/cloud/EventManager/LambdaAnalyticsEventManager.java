package com.flir.cloud.EventManager;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.flir.cloud.BuildConfig;
import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Created by mamar on 10/17/2017.
 */

public class LambdaAnalyticsEventManager {

    public static final String LAMBDA_EVENTS_TAG = "LambdaAnalytics";

    //Constants
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY = "Category";
    public static final String LAMBDA_EVENT_MANAGER_ACTION = "Action";
    public static final String LAMBDA_EVENT_MANAGER_EVENT = "Event";
    public static final String LAMBDA_EVENT_MANAGER_COMMENT = "Comment";

    //CATEGORIES
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_LOGIN_ACTIVITY = "LoginActivity";
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY = "AccountSelectionActivity";
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_DEVICE_SELECTION_CAROUSEL_ACTIVITY = "DeviceSelectionCarouselActivity";
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY = "CameraItemActivity";
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_MAIN_ACTIVITY = "MainActivity";
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY = "VolumesActivity";
    public static final String LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY = "VolumeItemActivity";

    //ACTIONS
        //LoginPage
    public static final String LAMBDA_EVENT_MANAGER_ACTION_OPEN_SIGN_UP_ACTIVITY = "OpenSignUpActivityClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_OPEN_FORGOT_PASSWORD_ACTIVITY = "OpenForgotPasswordActivityClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_LOGIN_BUTTON_CLICKED = "LogInButtonClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SIGN_UP_BUTTON_CLICKED = "SignUpButtonClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_RESET_PASSWORD_BUTTON_CLICKED = "ResetPasswordButtonClicked";
        //ChooseAccountPage
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SELECT_ACCOUNT_BUTTON_CLICKED = "SelectAccountButtonClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_ACCOUNT_SELECTION_MADE = "AccountSelectionHasBeenMade";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_CREATE_NEW_ACCOUNT_CLICKED = "CreateNewAccountClicked";
        //CarouselPage
    public static final String LAMBDA_EVENT_MANAGER_ACTION_DEVICE_ITEM_SELECTED = "DeviceItemSelected";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_ADD_DEVICE_ITEM_SELECTED = "AddDeviceItemSelected";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_ADD_DEVICE_BUTTON_CLICKED = "AddDeviceButtonClicked";
        //CameraItemPage
    public static final String LAMBDA_EVENT_MANAGER_ACTION_PLAY_LIVE_VIDEO_CLICKED = "playLiveVideoClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SHOW_VIDEO_CONTROLLER_CLICKED = "ShowVideoControllerClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_HIDE_VIDEO_CONTROLLER_CLICKED = "HideVideoControllerClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_FULL_SCREEN_BUTTON_CLICKED = "FullScreenButtonClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_OPEN_TIME_LINE_VIEW_CLICKED = "OpenTimeLineViewClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_CLOSE_TIME_LINE_VIEW_CLICKED = "CloseTimeLineViewClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_TIME_LINE_VIEW_TAPED = "TimeLineViewTaped";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_DELETE_DEVICE_CLICKED = "DeleteDeviceClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SHARE_DEVICE_CLICKED = "ShareDeviceClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_UN_SHARE_DEVICE_CLICKED = "UnShareDeviceClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_UPDATE_DESIRED_MADE = "SettingsUpdateDesiredHasBeenMade";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SETTING_UPDATE_DEVICE_CLICKED = "SettingUpdateDeviceClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_SETTINGS_TIMEZONE_CHANGED = "SettingsTimezoneChanged";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_EXPORT_VIDEO_CLICKED = "ExportVideoClicked";
       // MainActivity
    public static final String LAMBDA_EVENT_MANAGER_ACTION_DEVICES_VIEW_CLICKED = "DevicesViewItemClicked";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_STORAGE_VIEW_CLICKED = "StorageViewItemClicked";
       //VolumesPage
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_SELECTED = "VolumeItemSelected";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_CREATED = "VolumeItemCreated";
       //VolumeItemPage
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_CHOOSER_OPENED = "FileChooserOpened";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_UPLOADED = "FileUploaded";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_DELETED = "FileDeleted";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_RENAMED = "FileRenamed";
    public static final String LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_UPDATED = "FileUpdated";

    //EVENTS
    public static final String LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST = "sendRequest";
    public static final String LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE = "successResponse";
    public static final String LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE = "FailureResponse";

    //COMMENT
    public static final String LAMBDA_EVENT_MANAGER_COMMENT_UPDATE_DESIRED_RECORDING_MODE = "RecordingMode";
    public static final String LAMBDA_EVENT_MANAGER_COMMENT_UPDATE_DESIRED_RESOLUTION = "Resolution";


    private FirebaseAnalytics mFirebaseAnalytics;

    //private constructor to avoid client applications to use constructor
    public LambdaAnalyticsEventManager(Context aContext){
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(aContext);
    }

    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment){
        Bundle params = new Bundle();
        params.putString(LAMBDA_EVENT_MANAGER_ACTION, aAction);
        params.putString(LAMBDA_EVENT_MANAGER_EVENT, aEvent);
        params.putString(LAMBDA_EVENT_MANAGER_COMMENT, aComment);
        mFirebaseAnalytics.logEvent(aCategory, params);
        printEventLog(aCategory, aAction, aEvent, aComment);
    }

    private void printEventLog(String aCategory, String aAction, String aEvent, String aComment){
        if(BuildConfig.DEBUG){
            Log.d(LAMBDA_EVENTS_TAG ,"LambdaEventLog: " + "\n" +
                    "\t" + "Category: " + "\t" + aCategory + "\n" +
                    "\t" + "Action: " + "\t" + aAction + "\n" +
                    "\t" + "Event: " + "\t\t" + aEvent + "\n" +
                    "\t" + "Comment: " + "\t" + aComment + "\n");
        }
    }


}
