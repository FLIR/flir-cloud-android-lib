package com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView.ExportVideoNotificationFiles;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flir.cloud.ui.Access.AccessActivities.AccessCarouselFiles.AccessCameraItemView.CameraTopVideoView.TimeLineSelectorLinearLayout;
import com.flir.cloud.ui.fileExplorer.FoldersClasses.FolderActivity;

/**
 * Created by mamar on 10/23/2017.
 */

public class NotificationBroadcastReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_DELETE)){
            String AsyncKey = intent.getExtras().getString("AsyncCode");
            if(TimeLineSelectorLinearLayout.asyncTasksList.get(AsyncKey) != null) {
                TimeLineSelectorLinearLayout.asyncTasksList.get(AsyncKey).cancel(true);
                TimeLineSelectorLinearLayout.asyncTasksList.remove(AsyncKey);
            }else if (FolderActivity.uploadFileAsyncTasksList.get(AsyncKey) != null){
                FolderActivity.uploadFileAsyncTasksList.get(AsyncKey).cancel(true);
                FolderActivity.uploadFileAsyncTasksList.remove(AsyncKey);
            }else if (FolderActivity.downloadFileAsyncTasksList.get(AsyncKey) != null){
                FolderActivity.downloadFileAsyncTasksList.get(AsyncKey).cancel(true);
                FolderActivity.downloadFileAsyncTasksList.remove(AsyncKey);
            }
        }
    }
}
