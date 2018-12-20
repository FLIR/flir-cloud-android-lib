package com.flir.cloud.ui.fileExplorer.FoldersClasses;

import com.flir.sdk.models.Storage.GetFileResponse;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.presignedUploadUrl;

import java.io.File;

import okhttp3.ResponseBody;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public interface FolderView {

    void showWait();

    void removeWait();

    void onFailure(String appErrorMessage);

    void responseFromServer(VolumeFilesResponse response);

    void uploadFileToCloud(presignedUploadUrl response,String path, File pathFile, boolean isNewFile);

    void updateRecyclerView(GetFileResponse response);

    void updateNotification(int id, boolean isSucceed);

    void deleteFileSuccess(String aFileId);

    void writeFileToDisk(ResponseBody body, String fileName, int id);

    void sendEvent(String aCategory, String aAction, String aEvent, String aComment);




}
