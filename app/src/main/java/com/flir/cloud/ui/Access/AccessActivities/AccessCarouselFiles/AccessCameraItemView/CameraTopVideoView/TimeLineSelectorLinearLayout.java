package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.LambdaUtils;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView.ExportVideoNotificationFiles.NotificationBroadcastReceiver;
import com.flir.sdk.Interceptors.PlaybackInterceptor;
import com.flir.sdk.models.Playback.PlaybackUrlResponse;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat.Builder;

import java.io.File;
import java.util.HashMap;

/**
 * Created by Moti on 18-Jun-17.
 */

public class TimeLineSelectorLinearLayout extends LinearLayout implements ITopVideoViewSelectorView {

    Context mContext;

    private TimeLineViewPresenter mTimeLineViewPresenter;
    @Inject
    PlaybackInterceptor mPlaybackInterceptor;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;
    private LambdaSharedPreferenceManager mLambdaSharedPreferenceManager;

    private String mSerial;

    private Activity mActivity;
    private int videoTopHeight;

    @BindView(R.id.top_view_1_item)
    public LinearLayout mLinearLayoutTopViewItem1;

    @BindView(R.id.top_view_2_item)
    public LinearLayout mLinearLayoutTopViewItem2;

    @BindView(R.id.top_view_3_item)
    public LinearLayout mLinearLayoutTopViewItem3;

    @BindView(R.id.top_view_4_item)
    public LinearLayout mLinearLayoutTopViewItem4;

    @BindView(R.id.ll_from_to_text)
    public LinearLayout mLinearLayoutFromToText;

    @BindView(R.id.tv_export_from)
    public TextView mTextViewFrom;

    @BindView(R.id.tv_export_to)
    public TextView mTextViewTo;

    @BindView(R.id.tv_time_line_picker_export_from)
    public TextView mTextViewPickerFrom;

    @BindView(R.id.tv_time_line_picker_export_to)
    public TextView mTextViewPickerTo;

    @BindView(R.id.go_to_text_view)
    public TextView mGoToTextView;

    @BindView(R.id.tv_time_line_picker_go_to)
    public TextView mGoToTimeLinePicker;

    @BindView(R.id.ib_export_video)
    public ImageButton mExportVideoButton;
    @OnClick(R.id.ib_export_video)
    public void doExportVideoClicked() {
        if(LambdaUtils.checkExternalStoragePermission(mActivity)) {
            doExportVideo();
        }
    }


    public TimeLineSelectorLinearLayout(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public TimeLineSelectorLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public TimeLineSelectorLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ((MainApplication)mContext.getApplicationContext()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(mContext);
        mLambdaSharedPreferenceManager = LambdaSharedPreferenceManager.getInstance();
        View child = inflater.inflate(R.layout.video_top_editor_view, null);
        addView(child);
        ButterKnife.bind(this);
        initViewsSize();
    }

    public void initValues(String serial, Activity aActivity) {
        mSerial = serial;
        mActivity = aActivity;
        mTimeLineViewPresenter = new TimeLineViewPresenter(mPlaybackInterceptor,this);
    }

    private void initViewsSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        videoTopHeight = (int) getResources().getDimension(R.dimen.video_view_time_line_top_layout_height);

        ViewGroup.LayoutParams params1 = mLinearLayoutTopViewItem1.getLayoutParams();
        params1.width = (int) (width * 0.26);
        mLinearLayoutTopViewItem1.setLayoutParams(params1);

        ViewGroup.LayoutParams params2 = mLinearLayoutTopViewItem2.getLayoutParams();
        params2.width = (int) (width * 0.15);
        mLinearLayoutTopViewItem2.setLayoutParams(params2);


        ViewGroup.LayoutParams params3 = mLinearLayoutTopViewItem3.getLayoutParams();
        params3.width = (int) (width * 0.44);
        mLinearLayoutTopViewItem3.setLayoutParams(params3);


        ViewGroup.LayoutParams params4 = mLinearLayoutTopViewItem4.getLayoutParams();
        params4.width = (int) (width * 0.16);
        mLinearLayoutTopViewItem4.setLayoutParams(params4);


        ViewGroup.LayoutParams params5 = mGoToTextView.getLayoutParams();
        params5.height = (int) (videoTopHeight * 0.40);
        mGoToTextView.setLayoutParams(params5);

        ViewGroup.LayoutParams params6 = mGoToTimeLinePicker.getLayoutParams();
        params6.height = (int) (videoTopHeight * 0.60);
        mGoToTimeLinePicker.setLayoutParams(params6);

        int exportFromToTextViewWidth = mLinearLayoutTopViewItem3.getLayoutParams().width;

        ViewGroup.LayoutParams params7 = mTextViewFrom.getLayoutParams();
        params7.width = exportFromToTextViewWidth / 2;
        mTextViewFrom.setLayoutParams(params7);

        ViewGroup.LayoutParams params8 = mTextViewTo.getLayoutParams();
        params8.width = exportFromToTextViewWidth / 2;
        mTextViewTo.setLayoutParams(params8);

        ViewGroup.LayoutParams params9 = mTextViewPickerFrom.getLayoutParams();
        params9.width = exportFromToTextViewWidth / 2;
        mTextViewPickerFrom.setLayoutParams(params9);
    }

    public void doExportVideo() {
        long videoFromTimestamp = LambdaUtils.convertDateToTimestamp(mTextViewPickerFrom.getText().toString().replace("\n"," "));
        String iosLambdaFormatFromString = LambdaUtils.fromTimestampToISO(videoFromTimestamp);
        long videoToTimestamp = LambdaUtils.convertDateToTimestamp(mTextViewPickerTo.getText().toString().replace("\n"," "));
        String iosLambdaFormatToString = LambdaUtils.fromTimestampToISO(videoToTimestamp);
        mTimeLineViewPresenter.getExportVideo(mSerial, mLambdaSharedPreferenceManager.getSelectedChannel(mSerial), iosLambdaFormatFromString, iosLambdaFormatToString);

        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CAMERA_ITEM_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_EXPORT_VIDEO_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, iosLambdaFormatFromString + "/" + iosLambdaFormatToString);

    }

    @Override
    public void onFailure(String aMessage) {

    }

    @Override
    public void response(PlaybackUrlResponse aPlaybackUrlResponse,String startTime, String endTime) {
        startExportVideoDownloadWithAsyncTask(aPlaybackUrlResponse, startTime, endTime);
    }


    private void startExportVideoDownloadWithAsyncTask(PlaybackUrlResponse aPlaybackUrlResponse, String startTime, String endTime) {
        mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext);

        mBuilder.setContentTitle(mContext.getResources().getString(R.string.playback_service_export_file_download) + " " + startTime.replace("T"," ").replace("Z"," ") + "- " + endTime.replace("T"," ").replace("Z"," "))
                .setContentText(mContext.getResources().getString(R.string.playback_service_export_file_download_in_progress))
                .setSmallIcon(android.R.drawable.stat_sys_download);
        ExportFileDownloader asyncTasks = new ExportFileDownloader(aPlaybackUrlResponse.url, startTime, endTime);
        asyncTasks.execute();
        asyncTasksList.put(asyncTasks.hashCode() + "",asyncTasks);
    }

    //ExportFileDownloader asyncTask
    public static HashMap<String ,AsyncTask<Void, Integer, Boolean>> asyncTasksList = new HashMap<>();
    private NotificationManager mNotifyManager;
    private Builder mBuilder;

    private class ExportFileDownloader extends AsyncTask<Void, Integer, Boolean>{

        private String mStartTime;
        private String mEndTime;
        private int id;
        private String videoUrl;
        private Exception error;
        private String filePath;
        private String fileName;

        private ExportFileDownloader(String aUrl,String startTime, String endTime) {
            super();
            videoUrl = aUrl;
            mStartTime = startTime;
            mEndTime = endTime;
            id = hashCode();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mNotifyManager.notify(id, mBuilder.build());
            setDeleteIntentToCurrentNotification();
            fileName = mStartTime + "-" + mEndTime + ".mp4";
            filePath = Environment.getExternalStorageDirectory() + File.separator + LambdaConstant.FILE_EXPLORER_FILES_FOLDER_NAME;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            mBuilder.setProgress(100, values[0], false);
            mNotifyManager.notify(id, mBuilder.build());
            super.onProgressUpdate(values);
        }

        @Override
        protected Boolean doInBackground(Void... params) {
                mBuilder.setContentText(mContext.getResources().getString(R.string.playback_service_export_file_download_in_progress));
                mBuilder.setProgress(0, 0, false);
                mNotifyManager.notify(id, mBuilder.build());
                error = LambdaUtils.downloadFileFromServer(videoUrl, filePath, fileName);
            return error == null;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            mBuilder.setProgress(0, 0, false);
            if(result) {
                mBuilder.setContentText(mContext.getResources().getString(R.string.playback_service_export_file_download_complete));
                mBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                setContentIntentToCurrentNotification();
            }else{
                if (error != null) {
                    mBuilder.setContentText(mContext.getResources().getString(R.string.playback_service_export_file_download_failed) + ": " + error.getMessage());
                    mBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
                }
            }
            mNotifyManager.notify(id, mBuilder.build());
        }


        private void setContentIntentToCurrentNotification() {

            File folder = new File(filePath);
            boolean success = true;
            if (!folder.exists()) {
                success = folder.mkdir();
            }
            if (success) {
                File file = new File(filePath + "/" + fileName);
                String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(".mp4");

                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setDataAndType(FileProvider.getUriForFile(mContext, mContext.getApplicationContext().getPackageName() + ".com.flir.cloud.provider", file), mime);

                PendingIntent contentIntent =
                        PendingIntent.getActivity(mContext, 0, intent, 0);
                mBuilder.setContentIntent(contentIntent);
            }
        }

        private void setDeleteIntentToCurrentNotification() {

            Intent intent = new Intent(mContext, NotificationBroadcastReceiver.class);
            intent.putExtra("AsyncCode", hashCode()+"");
            intent.setAction(Intent.ACTION_DELETE);
            PendingIntent contentIntent = PendingIntent.getBroadcast(mContext, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            mBuilder.setDeleteIntent(contentIntent);
        }
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

}
