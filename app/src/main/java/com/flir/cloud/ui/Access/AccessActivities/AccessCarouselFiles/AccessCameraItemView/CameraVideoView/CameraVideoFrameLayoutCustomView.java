package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraVideoView;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.LambdaUtils;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraItemActivity;
import com.flir.cloud.ui.Views.TimeLineCustomView.SelectorCustomView.TLView;
import com.flir.sdk.Interceptors.StreamingInterceptor;
import com.flir.sdk.models.Events.GetUrlResponse;

import javax.inject.Inject;


public class CameraVideoFrameLayoutCustomView extends FrameLayout implements ICameraVideoItemView {

    public final static String URL_EXPIRATION_TIMESTAMP_KEY = "expirationTimestamp";
    public final static String STREAM_URL_KEY = "streamUrl";
    public final static long STREAM_URL_EXPIRATION_TIME = 600000;
    public final static int VIDEO_CONTROLLER_APPEARS_TIME_IN_MILLIS = 2000;

    public static final int GET_STREAM_URL_REQUEST_RETRY_IN_MILLIS = 2500;
    public static final int GET_STREAM_URL_REQUEST_NUMBER_OF_TRIES = 3;

    @Inject
    StreamingInterceptor mStreamingInterceptor;

    private Context mContext;
    private int mGetStreamUrlTryNumber;
    private CameraItemActivity mActivity;
    private CameraVideoItemPresenter mCameraVideoItemPresenter;
    private String mDeviceSerial;
    private String mThumbnailUrl;
    private String mChannel;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    private ImageView mThumbnailImageView;
    private ImageButton mPlayVideoButton;
    private TextView mSpeedIndicator;
    private CountDownTimer mCountDownTimer;
    private FrameLayout mThumbnailFrameLayout;
    private VideoView mExoMediaVideoView;
    private String mVideoUrl;
    private float videoSpeed;
    private TLView mTimeLine;
    private boolean mIsDownloadUrlFromAPI;

    private boolean forceVideoControllerON;

    long updateTimeLineView = 100;
    private Handler handler = new Handler();


    public CameraVideoFrameLayoutCustomView(Context context) {
        super(context);
    }

    public CameraVideoFrameLayoutCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        ((MainApplication)mContext.getApplicationContext()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(getContext());
        mCameraVideoItemPresenter = new CameraVideoItemPresenter(mStreamingInterceptor,this);
        initView();

    }

    public CameraVideoFrameLayoutCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public CameraVideoFrameLayoutCustomView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void initValues(String aDeviceSerial, String aThumbnailUrl, TLView aTimeLine, String aChannel, CameraItemActivity aActivity, boolean isDownloadUrlFromAPI){
        mDeviceSerial = aDeviceSerial;
        mThumbnailUrl = aThumbnailUrl;
        mActivity = aActivity;
        mChannel = aChannel;
        mTimeLine = aTimeLine;
        mIsDownloadUrlFromAPI = isDownloadUrlFromAPI;
        updateThumbnailPic();

        startVideoWithUrlFromAPI();

    }

    private void startVideoWithUrlFromAPI() {
        String urlFromPrefs = LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(getStreamUrlSharedPreferenceStringKey(), "");
        long urlExpirationTime = LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(getExpirationSharedPreferenceStringKey(), (long)0);

        if(urlFromPrefs.isEmpty() || (System.currentTimeMillis() - urlExpirationTime)  > STREAM_URL_EXPIRATION_TIME  || mIsDownloadUrlFromAPI) {
            getDownloadUrlFromAPI();
        }else {
            startPlayVideo(urlFromPrefs);
        }
    }

    private void getDownloadUrlFromAPI() {
        mCameraVideoItemPresenter.getDownloadVideoUrl(LambdaConstant.GRT_CAMERA_VIDEO_URL_HTTP_PROTOCOL, mDeviceSerial,LambdaConstant.GRT_CAMERA_VIDEO_URL_MP4_STREAM_FORMAT, mChannel);
    }

    public void initView() {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View deviceVideoCustomView = inflater.inflate(R.layout.device_video_custom_view, null);
        mThumbnailImageView = (ImageView)deviceVideoCustomView.findViewById(R.id.video_thumbnail_image);
        mThumbnailFrameLayout = (FrameLayout)deviceVideoCustomView.findViewById(R.id.fl_thumbnail_view);
        mPlayVideoButton = (ImageButton) deviceVideoCustomView.findViewById(R.id.play_video_button);
        mSpeedIndicator = (TextView) deviceVideoCustomView.findViewById(R.id.text_view_speed_indicator);
        mPlayVideoButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_PLAY_LIVE_VIDEO_CLICKED,
                        "", "");

                playVideoClicked();
            }
        });

        mExoMediaVideoView = (VideoView) deviceVideoCustomView.findViewById(R.id.exo_media_video_view_item);
        addView(deviceVideoCustomView);
        if(CameraItemActivity.isStartPlayingVideo){
            playVideoClicked();
        }

        //Speed default value = 1x
        videoSpeed = 1;
        mCountDownTimer = new CountDownTimer(2500,1000){
            @Override
            public void onTick(long millisUntilFinished){
            }

            @Override
            public void onFinish(){
                mSpeedIndicator.setText("");
            }
        }.start();

        getVideoTimeToPrint();

        mExoMediaVideoView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                forceVideoControllerON = true;
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        forceVideoControllerON = false;
                    }
                }, VIDEO_CONTROLLER_APPEARS_TIME_IN_MILLIS);

                return true;
            }
        });
    }


    private void playVideoClicked() {
        CameraItemActivity.isStartPlayingVideo =  true;
        mThumbnailFrameLayout.setVisibility(GONE);

        mExoMediaVideoView.setOnPreparedListener(new OnPreparedListener() {
            @Override
            public void onPrepared() {
                mExoMediaVideoView.start();
            }
        });

    }

    private void updateThumbnailPic() {

        if(mThumbnailUrl != null && !mThumbnailUrl.isEmpty()) {
            Glide.with(mContext)
                    .load(mThumbnailUrl)
                    .into(mThumbnailImageView);
            mThumbnailImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }


    @Override
    public void responseFromServer(GetUrlResponse aGetUrlResponse) {
        mVideoUrl = aGetUrlResponse.url;
        LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(getExpirationSharedPreferenceStringKey(),System.currentTimeMillis());
        LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(getStreamUrlSharedPreferenceStringKey(), mVideoUrl);

        startPlayVideo(mVideoUrl);
    }

    @Override
    public void onFailure(String reqName) {
        if(reqName.equals(CameraVideoItemPresenter.CAMERA_VIDEO_ITEM_PRESENTER_GET_DOWNLOAD_VIDEO_URL) && mGetStreamUrlTryNumber++ < GET_STREAM_URL_REQUEST_NUMBER_OF_TRIES){
                final Handler handler = new Handler();
                handler.postDelayed(() -> getDownloadUrlFromAPI(), GET_STREAM_URL_REQUEST_RETRY_IN_MILLIS);
        }else {
            mPlayVideoButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_warning));
            mPlayVideoButton.setClickable(false);
            mThumbnailFrameLayout.setVisibility(VISIBLE);
        }
    }

    public String getExpirationSharedPreferenceStringKey(){
        return URL_EXPIRATION_TIMESTAMP_KEY +  mChannel + mDeviceSerial;
    }

    public String getStreamUrlSharedPreferenceStringKey(){
        return STREAM_URL_KEY +  mChannel + mDeviceSerial;
    }

    public void startPlayVideo(String urlFromPrefs){
        mExoMediaVideoView.setVideoURI(Uri.parse(urlFromPrefs));
        mExoMediaVideoView.start();
    }

    public void setVideoSpeed(float aSpeed, boolean aNeedToPlay){
        videoSpeed = aSpeed;
        mExoMediaVideoView.setPlaybackSpeed(videoSpeed);
        mSpeedIndicator.setText(String.valueOf(videoSpeed) + "x");

        mCountDownTimer.cancel();
        mCountDownTimer.start();

        if(!mExoMediaVideoView.isPlaying() && aNeedToPlay){
            mExoMediaVideoView.start();
        }

    }

    public void getVideoTimeToPrint(){
        handler.postDelayed(new Runnable(){
            public void run(){
                if(mExoMediaVideoView.getVideoControls() != null) {
                    CharSequence timeFromStart = ((TextView) mExoMediaVideoView.getVideoControls().findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_current_time)).getText();
                    TextView time = ((TextView) mExoMediaVideoView.getVideoControls().findViewById(com.devbrackets.android.exomedia.R.id.exomedia_controls_end_time));
                    if (mActivity != null) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(mActivity.getVideoControllerMode() || forceVideoControllerON) {
                                    mExoMediaVideoView.getVideoControls().show();
                                }else {
                                    mExoMediaVideoView.getVideoControls().hide();
                                }
                                time.setText(LambdaUtils.convertTextToTimestamp(timeFromStart.toString(), "14:10:00"));
                                long speedTime = (long) (updateTimeLineView * videoSpeed);

                                if (videoSpeed > 1) {
                                    // long speedTime = (long) (1000 * videoSpeed);
                                    mTimeLine.takeTimeLineForward(updateTimeLineView + speedTime);
                                } else if (videoSpeed < 1) {

                                    mTimeLine.takeTimeLineForward(updateTimeLineView - speedTime);
                                }else{
                                    mTimeLine.takeTimeLineForward(updateTimeLineView);
                                }

                            }
                        });
                    }
                }
                handler.postDelayed(this, updateTimeLineView);
            }
        }, updateTimeLineView);
    }

    public float getVideoSpeed(){
        return videoSpeed;
    }

    public void removeThumbnail(){
        mThumbnailFrameLayout.setVisibility(View.GONE);
    }

    public void playLiveVideo(){



        if(mVideoUrl != null) {
            startPlayVideo(mVideoUrl);
        }else{
            getDownloadUrlFromAPI();
        }

        if(videoSpeed != 1) {
            setVideoSpeed(1, false);
        }

    }

}
