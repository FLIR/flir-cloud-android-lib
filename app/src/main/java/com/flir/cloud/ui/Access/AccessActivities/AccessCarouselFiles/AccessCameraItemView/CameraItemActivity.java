package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView;

import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.LambdaUtils;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraVideoView.CameraVideoFrameLayoutCustomView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselItemFragment;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselSettingsDialogFiles.CarouselSettingsDialogView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.CarouselEffectFiles.CarouselShareDialogFiles.CarouselShareDialogView;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.cloud.ui.Views.TimeLineCustomView.IUpdateTimeSelector;
import com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView.TLView;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView.TimeLineSelectorLinearLayout;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.network.AuthenticationProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class CameraItemActivity extends AppCompatActivity implements IUpdateTimeSelector, ICameraActivityItemView ,IVideoViewAction{


    public static final double MIN_VIDEO_PLAYBACK_SPEED = 0;
    public static final double MAX_VIDEO_PLAYBACK_SPEED = 2.5;
    @Inject
    DeviceInterceptor DeviceInterceptor;

    @Inject
    AuthenticationProvider authenticationProvider;

    CameraActivityItemPresenter presenter;

    @BindView(R.id.btn_btn_video_view_settings)
    public ImageButton mBtnVideoSettings;

    @BindView(R.id.btn_video_view_timeline_picker)
    public ImageButton openTimeLineViewButton;

    @BindView(R.id.btn_video_view_timeline_picker_close)
    public ImageButton closeTimeLineViewButton;

    @BindView(R.id.lambda_custom_progress_bar_device_item)
    public LambdaCustomProgressBar lambdaCustomProgressBar;

    @OnClick(R.id.btn_video_view_timeline_picker_speed_minus)
    public void speedDownClicked() {
        doSpeedDownClicked();
    }

    @OnClick(R.id.btn_video_view_timeline_picker_speed_plus)
    public void speedUpClicked() {
        doSpeedUpClicked();
    }

    @OnClick(R.id.btn_video_view_timeline_picker)
    public void openTimeLinePickerClicked() {
        doOpenTimeLinePickerClicked();
    }

    @OnClick(R.id.btn_video_view_timeline_picker_close)
    public void closeTimeLinePickerClicked() {
        doCloseTimeLinePickerClicked();
    }

    @OnClick(R.id.btn_btn_video_view_settings)
    public void settingsIconClicked() {
        doSettingsIconClicked();
    }

    @OnClick(R.id.btn_btn_video_view_delete)
    public void deleteIconClicked() {
        doDeleteIconClicked();
    }

    @OnClick(R.id.btn_btn_video_controls_on_off)
    public void videoControlsClicked() {
        doVideoControlsClicked();
    }

    @BindView(R.id.btn_btn_video_controls_on_off)
    public ImageButton mVideoControlsMode;

    @OnClick(R.id.btn_video_view_timeline_picker_controller_on_off)
    public void videoTimeLineControlsClicked() {
        doVideoTimeLineControlsClicked();
    }

    @BindView(R.id.btn_video_view_timeline_picker_controller_on_off)
    public ImageButton mVideoTimeLineControlsMode;

    @BindView(R.id.btn_btn_video_view_delete)
    public ImageButton mDeleteDevice;

    @BindView(R.id.btn_btn_video_view_share_device)
    public ImageButton mShareDevice;

    @OnClick(R.id.btn_btn_video_view_full_screen)
    public void goFullScreenMode() {
      goFullScreen();
    }

    @OnClick(R.id.btn_btn_video_view_share_device)
    public void doShareDeviceClicked() {
        doShareDevice();
    }

    @BindView(R.id.access_view_tlv_view)
    public LinearLayout mLlTlvLayout;

    @BindView(R.id.access_view_carousel_item_buttons_view)
    public LinearLayout mLlButtonsView;

    @BindView(R.id.time_line_buttons_view)
    public LinearLayout mLlSpeedView;

    @BindView(R.id.access_tlv)
    public TLView mTimeLine;

    @BindView(R.id.ll_video_bottom)
    public LinearLayout mLinearLayoutBottomView;

    @BindView(R.id.ll_video_page_view)
    public LinearLayout mActivityLinearLayout;

    @BindView(R.id.ll_video_top)
    public TimeLineSelectorLinearLayout mLinearLayoutTopView;

    @BindView(R.id.tv_time_line_picker_export_from)
    public TextView mTextViewPickerFrom;

    @BindView(R.id.tv_time_line_picker_export_to)
    public TextView mTextViewPickerTo;

    /*@BindView(R.id.ib_top_channel_selector)
    public ImageButton mChannelSelector;*/

    @BindView(R.id.ll_select_channel)
    public LinearLayout mLinearLayoutChannelSelector;

  /*  @OnClick(R.id.ib_top_channel_selector)
    public void doChannelSelectorImageButtonClicked() {
        doChannelSelector();
    }*/

    @OnClick(R.id.tv_top_channel_selector)
    public void doChannelSelectorTestViewClicked() {
        doChannelSelector();
    }

    @OnClick(R.id.tv_top_show_device_state)
    public void doCShowDeviceStateClicked() {
        doDeviceState();
    }

    @BindView(R.id.tv_time_line_picker_go_to)
    public TextView mGoToTimeLinePicker;

    @BindView(R.id.camera_video_frame_layout_custom_view_xml)
    public CameraVideoFrameLayoutCustomView mCameraVideoFrameLayoutCustomView;

    private List<String> mChannelList;
    private String mChannelSelectedName;
    private boolean videoControlsMode;
    private int videoTopHeight;
    private int videoBottomHeight;
    private String serial;
    private String name;
    private String accountID;
    private String thumbnailUrl;
    public static boolean isStartPlayingVideo = false;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    private LambdaSharedPreferenceManager mLambdaSharedPreferenceManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.camera_activity_item_view);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        presenter = new CameraActivityItemPresenter(DeviceInterceptor, this);
        mLambdaSharedPreferenceManager = LambdaSharedPreferenceManager.getInstance();
        initCarouselItemDetails();
        mChannelSelectedName = mLambdaSharedPreferenceManager.getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + serial, "");

        mLinearLayoutTopView.initValues(serial, this);
        mTimeLine.initParam(serial, this);
        setDeleteAndShareResourceVisibility();

        videoControlsMode = false;

        presenter.getDeviceState(serial, false);
    }


    @Override
    public void responseFromServer(List<GetDeviceStateResponse> response, boolean isShowDialogWithDetails) {
        mChannelList = new ArrayList<>();
        for (int i = 0; i < response.size(); i++) {
            if(response.get(i).key.equals(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SUPPORTED_CHANNEL)){
                mChannelList = new ArrayList<>(Arrays.asList((response.get(i).reportedValue).split(",")));
            }


        }

        String channelPref = "";
        for (String s : mChannelList) {
            channelPref = s + ",";
        }
        mLambdaSharedPreferenceManager.setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SUPPORTED_CHANNEL + serial, channelPref);

        if(mChannelList.size() <= 1){
            mLinearLayoutChannelSelector.setVisibility(View.GONE);
        }

        if((mChannelSelectedName == null || mChannelSelectedName.isEmpty()) && mChannelList.size() > 0){
            mChannelSelectedName = mChannelList.get(0);
            mLambdaSharedPreferenceManager.setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + serial, mChannelSelectedName);
        }
        mCameraVideoFrameLayoutCustomView.initValues(serial,thumbnailUrl, mTimeLine, mChannelSelectedName, this, false);

        if(isShowDialogWithDetails){
            showStateDialog(response);
        }

    }

    private void showStateDialog(List<GetDeviceStateResponse> response) {
        //TODO Show list.
        //showDialogAlert();
        stateDialogCustomView mStateDialogCustomView = new stateDialogCustomView(this, response);

        new MaterialDialog.Builder(this)
                .title("State List")
                .customView(mStateDialogCustomView, false)
                .positiveText(R.string.dialog_done).show();


    }



    private void initCarouselItemDetails(){
        serial = getIntent().getExtras().getString(CarouselItemFragment.ID);
        name = getIntent().getExtras().getString(CarouselItemFragment.NAME);
        accountID = getIntent().getExtras().getString(CarouselItemFragment.ACCOUNT_ID);
        //TODO - Update THUMBNAIL URL
        thumbnailUrl = getIntent().getExtras().getString(CarouselItemFragment.DRAWABLE_THUMBNAIL_RESOURCE);
    }

    //If you've been shared to resource - you can not delete or share it.
    private void setDeleteAndShareResourceVisibility() {
        if (!accountID.equals(authenticationProvider.getAccountID())){
            mDeleteDevice.setVisibility(View.GONE);
            mShareDevice.setVisibility(View.GONE);
        }else{
            mDeleteDevice.setVisibility(View.VISIBLE);
            mShareDevice.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        initViews();
    }

    private void initViews() {
        mTimeLine.setInterfaceView(this);
        videoBottomHeight = (int) getResources().getDimension(R.dimen.video_view_time_line_bottom_layout_height);

        ViewGroup.LayoutParams params = mLlTlvLayout.getLayoutParams();
        params.height = videoBottomHeight ;
        mLlTlvLayout.setLayoutParams(params);

    }

    private void doChannelSelector() {

        String selectedChannel = mLambdaSharedPreferenceManager.getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + serial, "");
        new MaterialDialog.Builder(this)
                .title(getResources().getString(R.string.device_select_channel) + " - " + serial)
                .items(mChannelList)
                .itemsCallbackSingleChoice(mChannelList.indexOf(selectedChannel), new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        if(text != null && !selectedChannel.equals(text.toString())) {
                            onChannelSelectionHasMade(text);
                        }
                        return true;
                    }
                })
                .positiveText(R.string.device_select_channel)
                .show();
    }

    private void doDeviceState() {
        presenter.getDeviceState(serial, true);
    }

    private void onChannelSelectionHasMade(CharSequence which) {
        mLambdaSharedPreferenceManager.setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_SELECTED_CHANNEL + serial, which.toString());
        mChannelSelectedName = which.toString();

        mCameraVideoFrameLayoutCustomView.initValues(serial,thumbnailUrl, mTimeLine, mChannelSelectedName, this, true);


    }


    private void doSettingsIconClicked() {

        CarouselSettingsDialogView csv = new CarouselSettingsDialogView(this, serial, name, accountID);

        mLinearLayoutTopView.setVisibility(View.GONE);
        mLlTlvLayout.setVisibility(View.GONE);

        new MaterialDialog.Builder(this)
                .title(serial + " - " + getResources().getString(R.string.carousel_item_settings_dialog))
                .customView(csv, false)
                .positiveText(R.string.dialog_done)
                .show();
    }


    private void doDeleteIconClicked() {
        showDialogAlert();
    }

    private void doVideoControlsClicked() {
        videoControlsMode = !videoControlsMode;
        setVideoControllerVisibility(true);
    }

    private void doVideoTimeLineControlsClicked() {
        videoControlsMode = !videoControlsMode;
        setVideoControllerVisibility(true);
    }

    private void setVideoControllerVisibility(boolean aSendEvent) {
        if(videoControlsMode){
            mVideoTimeLineControlsMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_video_controls_on));
            mVideoControlsMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_video_controls_on));
        }else{
            mVideoTimeLineControlsMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_video_controls_off));
            mVideoControlsMode.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_video_controls_off));
        }

        if(aSendEvent) {
            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                    videoControlsMode ? LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SHOW_VIDEO_CONTROLLER_CLICKED : LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_HIDE_VIDEO_CONTROLLER_CLICKED,
                    "", "");
        }

    }

    public boolean getVideoControllerMode(){
        return videoControlsMode;
    }

    private void showDialogAlert() {
        mLlTlvLayout.setVisibility(View.GONE);
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    doDeleteDevice();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_delete_massage_massage) + " " + serial + "\u003F").setPositiveButton(getResources().getString(R.string.dialog_massage_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.dialog_massage_no), dialogClickListener).show();
    }

    private void doDeleteDevice() {
        presenter.deleteResource(serial);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_DELETE_DEVICE_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, serial);
    }


    private void doOpenTimeLinePickerClicked() {

        mLlTlvLayout.setVisibility(View.VISIBLE);
            mTimeLine.moveToCurrentTime();
            mTimeLine.getClipsFromServer();
            LambdaUtils.doChangeHeightAnimOnView(mLlTlvLayout, 1000, 0, videoBottomHeight );
            animateVideoTopViewFromBottomToTop();

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_OPEN_TIME_LINE_VIEW_CLICKED,
                "", "");

        setVideoControllerVisibility(false);

    }



    private void doSpeedUpClicked() {
        float videoSpeed = mCameraVideoFrameLayoutCustomView.getVideoSpeed();
        double newValue = Math.round((videoSpeed + 0.1f)*100.0)/100.0;
        if(videoSpeed < MAX_VIDEO_PLAYBACK_SPEED) {
            mCameraVideoFrameLayoutCustomView.setVideoSpeed((float) newValue, true);
        }

    }
    private void doSpeedDownClicked() {
        float videoSpeed = mCameraVideoFrameLayoutCustomView.getVideoSpeed();
        double newValue = Math.round((videoSpeed - 0.1f)*100.0)/100.0;
        if(videoSpeed > MIN_VIDEO_PLAYBACK_SPEED) {
            mCameraVideoFrameLayoutCustomView.setVideoSpeed((float) newValue, true);
        }
    }

    private void doCloseTimeLinePickerClicked() {

        mCameraVideoFrameLayoutCustomView.playLiveVideo();
        LambdaUtils.doChangeHeightAnimOnView(mLlTlvLayout, 1000, videoBottomHeight ,0);
        animateVideoTopViewFromTopToBottom();
        removeSpeedBar();

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_CLOSE_TIME_LINE_VIEW_CLICKED,
                "", "");

    }

    public void animateVideoTopViewFromTopToBottom(){

        mLinearLayoutTopView.setVisibility(LinearLayout.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.from_top_to_bottom);
        animation.setDuration(1000);
        mLinearLayoutTopView.setAnimation(animation);
        mLinearLayoutTopView.animate();
        animation.start();
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mLinearLayoutTopView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    public void showSpeedBar(){
        mLlButtonsView.setVisibility(View.GONE);
        mLlSpeedView.setVisibility(View.VISIBLE);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.view_fade_in_anim);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.view_fade_out_anim);
        mLlSpeedView.startAnimation(fadeInAnimation);
        mLlButtonsView.startAnimation(fadeOutAnimation);

    }
    public void removeSpeedBar(){
        mLlSpeedView.setVisibility(View.GONE);
        mLlButtonsView.setVisibility(View.VISIBLE);
        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.view_fade_in_anim);
        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.view_fade_out_anim);
        mLlSpeedView.startAnimation(fadeOutAnimation);
        mLlButtonsView.startAnimation(fadeInAnimation);

    }

    public void animateVideoTopViewFromBottomToTop(){

        mLinearLayoutTopView.setVisibility(LinearLayout.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.from_bottom_to_top);
        animation.setDuration(1000);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                showSpeedBar();
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mLinearLayoutTopView.setAnimation(animation);
        mLinearLayoutTopView.animate();
        animation.start();
    }


    @Override
    public void onBackPressed() {
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            CameraItemActivity.isStartPlayingVideo = false;
            finish();
        }else{
            exitFullScreen();
        }
    }


    @Override
    public void updateLeftText(String text) {
        mTextViewPickerFrom.setText(text);
    }

    @Override
    public void updateRightText(String text) {
        mTextViewPickerTo.setText(text);
    }

    @Override
    public void updateGoToText(String text) {
        mGoToTimeLinePicker.setText(text);
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
    public void responseFromServer(int action) {
        switch (action) {
            case CameraActivityItemPresenter.RESPONSE_BODY_ACTION_DELETE:
                setResult(CarouselItemFragment.DELETE_DEVICE_RESULT_CODE);
                finish();
                break;
            case CameraActivityItemPresenter.RESPONSE_BODY_ACTION_SHARE:
                Toast.makeText(this,"share success", Toast.LENGTH_SHORT).show();
                break;
        }

    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

    private void doShareDevice() {
        mLlTlvLayout.setVisibility(View.GONE);
        CarouselShareDialogView csv = new CarouselShareDialogView(this, serial);

        new MaterialDialog.Builder(this)
                .customView(csv, false)
                .positiveText(R.string.dialog_done)
                .show();

    }

    @Override
    public void playPlaybackVideo(String url) {
        mCameraVideoFrameLayoutCustomView.startPlayVideo(url);
    }

    @Override
    public void returnToPlayLive() {
        mCameraVideoFrameLayoutCustomView.playLiveVideo();
    }

    @Override
    public void removeThumbnail() {
        mCameraVideoFrameLayoutCustomView.removeThumbnail();
    }

    @Override
    public void addThumbnail() {

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            returnAllVideoScreenElements();
        } else {
            hideAllVideoScreenElements();
        }
    }

    public void hideAllVideoScreenElements() {
        for (int i = 0; i < mActivityLinearLayout.getChildCount(); i++) {
            View child = mActivityLinearLayout.getChildAt(i);
            child.setVisibility(View.GONE);
        }
        mCameraVideoFrameLayoutCustomView.setVisibility(View.VISIBLE);
    }

    public void returnAllVideoScreenElements() {
        for (int i = 0; i < mActivityLinearLayout.getChildCount(); i++) {
            View child = mActivityLinearLayout.getChildAt(i);
            child.setVisibility(View.VISIBLE);
        }
    }

    private void goFullScreen() {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_FULL_SCREEN_BUTTON_CLICKED,
                "", "");

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void exitFullScreen() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == LambdaUtils.REQUEST_EXTERNAL_PERMISSION_CODE) {
                // Continue with your action after permission request succeed
                mLinearLayoutTopView.doExportVideo();
            }
        }
    }

}
