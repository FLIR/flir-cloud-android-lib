package com.flir.cloud.ui.fileExplorer.FoldersClasses;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.Services.MqttTopicType;
import com.flir.cloud.Services.paho.MqttPublisher;
import com.flir.cloud.SharedPreferences.LambdaSharedPreferenceManager;
import com.flir.cloud.Utils.FileUtils;
import com.flir.cloud.di.DaggerApplicationComponent;
import com.flir.cloud.di.NetworkModule;
import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView.ExportVideoNotificationFiles.NotificationBroadcastReceiver;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;

import com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderRecyclerViewfiles.FolderRecyclerViewAdapter;
import com.flir.sdk.Interceptors.StorageInterceptor;
import com.flir.sdk.models.Storage.GetFileResponse;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.presignedUploadUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by Moti on 31-Jul-17.
 */

public class FolderActivity extends Activity implements FolderView, SharedPreferences.OnSharedPreferenceChangeListener {

    public static final int MAX_DISPLAY_SPAN_COUNT = 8;
    public static final int MIN_DISPLAY_SPAN_COUNT = 2;

    public static final int UPLOAD_FILE_SELECT_CODE = 111;
    public static final int UPDATE_FILE_SELECT_CODE = 222;

    @Inject
    StorageInterceptor mStorageInterceptor;
    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    private GridLayoutManager lLayout;
    private FolderPresenter mFolderPresenter;
    private ArrayList<FileItemObject> mFilesItems;

    private String volumeId;
    private String volumeName;
    FolderRecyclerViewAdapter mFolderRecyclerViewAdapter;
    private boolean mIsVolume;
    private String mFileIdToUpdate;

    private LambdaSharedPreferenceManager lambdaSharedPreferenceManager;
    private NotificationManager mNotificationManager;
    private NotificationCompat.Builder mNotificationBuilder;
    public static HashMap<String ,AsyncTask<Void, Void, Void>> uploadFileAsyncTasksList = new HashMap<>();
    public static HashMap<String ,AsyncTask<Void, Void, Boolean>> downloadFileAsyncTasksList = new HashMap<>();
    public static HashMap<String ,Integer> notificationListIds = new HashMap<>();

    @BindView(R.id.file_explorer_folder_grid_view)
    RecyclerView mFolderGridRecyclerView;

    @BindView(R.id.tv_folder_files)
    TextView mFolderFilesTitle;

    @BindView(R.id.ll_folder_tool_bar_container)
    LinearLayout mLlToolBarContainer;

    @BindView(R.id.lambda_custom_progress_bar_folder_main_page)
    LambdaCustomProgressBar progressBar;

    @OnClick(R.id.ib_cloud_storage_folder_display_option)
    void DisplayOptionButtonClicked(){
        doDisplayOptionClicked();
    }

    @OnClick(R.id.btn_tool_bar_add_file)
    void addFileClicked() {
        showFileChooser(UPLOAD_FILE_SELECT_CODE);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer_folder);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        DaggerApplicationComponent.builder().networkModule(new NetworkModule());

        ButterKnife.bind(this);
        mFolderPresenter = new FolderPresenter(mStorageInterceptor, this);

        lambdaSharedPreferenceManager = LambdaSharedPreferenceManager.getInstance();
        lambdaSharedPreferenceManager.getLambdaSharedPreference().registerOnSharedPreferenceChangeListener(this);

        volumeId = getIntent().getStringExtra(LambdaConstant.FILE_EXPLORER_VOLUME_ID);
        volumeName = getIntent().getStringExtra(LambdaConstant.FILE_EXPLORER_FILES_FOLDER_NAME);
        mFolderFilesTitle.setText(mFolderFilesTitle.getText().toString() + "\\" + volumeName);
        mIsVolume = volumeId != null;
        if (mIsVolume) {
            mFolderPresenter.getVolumeFiles(volumeId, 100);

            mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_SELECTED,
                    LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, volumeId);
        }
    }

    public FolderPresenter getFolderPresenter() {
        return mFolderPresenter;
    }


    @Override
    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void deleteFileSuccess(String aFileId){
        int fileIndex = getFileListIndex(aFileId);
        if(fileIndex != -1) {
            mFilesItems.remove(fileIndex);
            mFolderRecyclerViewAdapter.notifyDataSetChanged();
        }
    }

    private int getFileListIndex(String aFileId) {
        for (int i = 0; i < mFilesItems.size(); i++) {
            if(mFilesItems.get(i).getFileId().equals(aFileId))
                return i;
        }
        return -1;
    }

    @Override
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void updateRecyclerView(GetFileResponse response) {
        int fileListIndex = getFileListIndex(response.fileId);
        FileItemObject fileItem = new FileItemObject(response.fileId, response.volume, response.fileName, response.created, response.modified, response.size);

        if(fileListIndex != -1){
            mFilesItems.remove(fileListIndex);
            mFilesItems.add(fileListIndex,fileItem);
        }else{
            mFilesItems.add(fileItem);
        }
        mFolderRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String appErrorMessage) {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void responseFromServer(VolumeFilesResponse response) {
        initFilesList(response);
    }

    @Override
    public void writeFileToDisk(ResponseBody body, String fileName, int aId) {

        initNotificationObjects();
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download);
        new AsyncTask<Void, Void, Boolean>() {

            private int id;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                id = Math.abs(aId);
                setDeleteIntentToCurrentNotification(id);
                downloadFileAsyncTasksList.put(id + "", this);
                mNotificationManager.notify(id, mNotificationBuilder.build());
            }


            @Override
            protected Boolean doInBackground(Void... voids) {
                mNotificationBuilder.setContentTitle(getResources().getString(R.string.cloud_storage_lambda_notification_content_download) + " " + fileName + " ")
                        .setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_download) + " "
                                + getResources().getString(R.string.cloud_storage_lambda_notification_content_in_progress));
                mNotificationManager.notify(id, mNotificationBuilder.build());
                return FileUtils.writeResponseBodyToDisk(body, fileName);
            }

            @Override
            protected void onPostExecute(Boolean aBoolean) {
                super.onPostExecute(aBoolean);
                if(aBoolean){
                    mNotificationBuilder.setContentTitle(getResources().getString(R.string.cloud_storage_lambda_notification_content_download) + " " + fileName + " ").
                            setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_download) + " " + getResources().getString(R.string.cloud_storage_lambda_notification_content_complete));
                    mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_download_done);
                    downloadFileAsyncTasksList.remove(id + "");
                    setContentIntentToCurrentNotification(fileName);
                }else{
                    if(downloadFileAsyncTasksList.get(id + "") != null) {
                        mNotificationBuilder.setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_download) + " " + getResources().getString(R.string.cloud_storage_lambda_notification_content_failed));
                        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
                    }
                }
                mNotificationManager.notify(id, mNotificationBuilder.build());
            }
        }.execute();


    }

    private void setContentIntentToCurrentNotification(String fileName) {
        String filePath = Environment.getExternalStorageDirectory() + File.separator + LambdaConstant.FILE_EXPLORER_FILES_FOLDER_NAME;
        File folder = new File(filePath);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }
        if (success) {
            File file = new File(filePath + "/" + fileName);
            String mime = MimeTypeMap.getSingleton().getMimeTypeFromExtension(FileUtils.getFileContentType(fileName));
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".com.flir.cloud.provider", file), mime);

            PendingIntent contentIntent =
                    PendingIntent.getActivity(this, 0, intent, 0);
            mNotificationBuilder.setContentIntent(contentIntent);
        }
    }

    private void initNotificationObjects() {
        if(mNotificationManager == null){
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if(mNotificationBuilder == null){
            mNotificationBuilder = new  NotificationCompat.Builder(this);
        }
    }

    @Override
    public void uploadFileToCloud(presignedUploadUrl response, String path, File aFile, boolean isNewFile) {

        initNotificationObjects();
        mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_upload);

        new AsyncTask<Void, Void, Void>() {

            private int id;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                id = Math.abs(aFile.hashCode());
                notificationListIds.put(response.fileId, id);
                setDeleteIntentToCurrentNotification(id);
                uploadFileAsyncTasksList.put(id + "", this);
                mNotificationManager.notify(id, mNotificationBuilder.build());
            }

            @Override
            protected void onProgressUpdate(Void... voids) {
                mNotificationManager.notify(id, mNotificationBuilder.build());
                super.onProgressUpdate();
            }

            @Override
            protected Void doInBackground(Void... voids) {

                RequestBody requestBody = MultipartBody.create(MediaType.parse(FileUtils.getMineTypeFromFile(aFile)),aFile);
                if(isNewFile) {
                    mFolderPresenter.uploadFile(requestBody, response.url, response.fileId, FileUtils.getMineTypeFromFile(aFile), id);
                }else{
                    mFolderPresenter.updateFile(requestBody, mFileIdToUpdate , response.url,LambdaConstant.SERVER_AES256_ENCRYPTION, FileUtils.getMineTypeFromFile(aFile), id, aFile);
                }
                showRelevantNotification(isNewFile, aFile);
                mNotificationManager.notify(id, mNotificationBuilder.build());
                return null;
            }
        }.execute();

    }

    @Override
    public void updateNotification(int id, boolean isSucceed) {
        if(isSucceed) {
            mNotificationBuilder.setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_complete));
            mNotificationBuilder.setSmallIcon(android.R.drawable.stat_sys_upload_done);
            mNotificationManager.notify(id, mNotificationBuilder.build());
            uploadFileAsyncTasksList.remove(id + "");
        }else{
            if(uploadFileAsyncTasksList.get(id + "") != null) {
                mNotificationBuilder.setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_failed));
                mNotificationBuilder.setSmallIcon(android.R.drawable.stat_notify_error);
                mNotificationManager.notify(id, mNotificationBuilder.build());
            }
        }
    }

    private void showRelevantNotification(boolean isNewFile, File aFile) {
        if(isNewFile){
            mNotificationBuilder.setContentTitle(getResources().getString(R.string.cloud_storage_lambda_notification_content_upload) + " " + aFile.getName() + " ")
                    .setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_upload) + " " + getResources().getString(R.string.cloud_storage_lambda_notification_content_in_progress));
        }else {
            mNotificationBuilder.setContentTitle(getResources().getString(R.string.cloud_storage_lambda_notification_content_update) + " " + aFile.getName() + " ")
                    .setContentText(getResources().getString(R.string.cloud_storage_lambda_notification_content_update) + " " + getResources().getString(R.string.cloud_storage_lambda_notification_content_in_progress));
        }
    }

    private void setDeleteIntentToCurrentNotification(int id) {

        Intent intent = new Intent(this, NotificationBroadcastReceiver.class);
        intent.putExtra("AsyncCode", id +"");
        intent.setAction(Intent.ACTION_DELETE);
        PendingIntent contentIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        mNotificationBuilder.setDeleteIntent(contentIntent);
    }

    public void showFileChooser(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    requestCode);
        } catch (android.content.ActivityNotFoundException ex) {

        }

          mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
            LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_CHOOSER_OPENED,
            "", "");

    }

    public void showFileChooser(int requestCode, String fileId) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        mFileIdToUpdate = fileId;
        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    requestCode);
        } catch (android.content.ActivityNotFoundException ex) {

        }
    }

     @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         String fileName = "";
         String MineType = "";
        if ((requestCode == UPLOAD_FILE_SELECT_CODE) && (resultCode == -1)) {

            String path = FileUtils.getRealPath(this, data.getData());

            if (path != null) {
                File file = new File(path);
                fileName = FileUtils.getFileNameFromFilePath(path);
                MineType = FileUtils.getMineTypeFromFile(file);
                mFolderPresenter.presignedUploadUrl(file, path, volumeId, fileName, MineType);

                mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_UPLOADED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, fileName + "/" + MineType);
            }
        }
        else if ((requestCode == UPDATE_FILE_SELECT_CODE) && (resultCode == -1)) {

            String path = FileUtils.getRealPath(this, data.getData());
            if (path != null) {
                File file = new File(path);
                fileName = FileUtils.getFileNameFromFilePath(path);
                MineType = FileUtils.getMineTypeFromFile(file);
                mFolderPresenter.presignedUpdateUrl(file, mFileIdToUpdate, path, MineType);

                mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_UPDATED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, fileName + "/" + MineType);
            }
        }
    }

    private void doDisplayOptionClicked() {
        int SpanCount = lLayout.getSpanCount();
        int filesCount = mFilesItems.size();
        if(filesCount > SpanCount++ && SpanCount <= MAX_DISPLAY_SPAN_COUNT) {
            LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT, SpanCount);
            lLayout.setSpanCount(SpanCount);
        }else{
            LambdaSharedPreferenceManager.getInstance().setLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT, MIN_DISPLAY_SPAN_COUNT);
            lLayout.setSpanCount(MIN_DISPLAY_SPAN_COUNT);
        }
    }

    private void initFilesList(VolumeFilesResponse response) {

        mFilesItems = new ArrayList<>();

        GetFileResponse responseItem;
        FileItemObject fileItem;
        for (int i = 0; i < response.files.length; i++) {
            responseItem = response.files[i];
            fileItem = new FileItemObject(responseItem.fileId, responseItem.volume, responseItem.fileName, responseItem.created, responseItem.modified, responseItem.size);
            mFilesItems.add(fileItem);
        }

        initRecyclerView();
    }

    private void initRecyclerView() {

        lLayout = new GridLayoutManager(this, LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(LambdaSharedPreferenceManager.SHARED_PREFERENCE_DISPLAY_OPTION_SPAN_COUNT, 4));
        mFolderGridRecyclerView.setLayoutManager(lLayout);
        mFolderRecyclerViewAdapter = new FolderRecyclerViewAdapter(this, mFilesItems, mLlToolBarContainer);
        mFolderGridRecyclerView.setAdapter(mFolderRecyclerViewAdapter);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if(key.startsWith(MqttTopicType.MQTT_TOPIC_TYPE_FILE.getCode())) {
            String fileId = key.split("/")[1];
            if (LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(key, "NONE").equals(MqttPublisher.MQTT_FILE_MESSAGE_SUBJECT_CREATE)) {
                mFolderPresenter.getFileForMqttEvent(fileId);
                if(notificationListIds.containsKey(fileId)) {
                    updateNotification(notificationListIds.get(fileId), true);
                }
            } else if (LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(key, "NONE").equals(MqttPublisher.MQTT_FILE_MESSAGE_SUBJECT_DELETE)) {
                deleteFileSuccess(fileId);
            }else if (LambdaSharedPreferenceManager.getInstance().getLambdaPrefsValue(key, "NONE").equals(MqttPublisher.MQTT_FILE_MESSAGE_SUBJECT_UPDATE)) {
                mFolderPresenter.getFileForMqttEvent(key);
                if(notificationListIds.containsKey(fileId)) {
                    updateNotification(notificationListIds.get(fileId), true);
                }
            }
        }
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

}
