package com.flir.cloud.ui.fileExplorer.FoldersClasses;

import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.sdk.Interceptors.StorageInterceptor;
import com.flir.sdk.models.Storage.GetFileResponse;
import com.flir.sdk.models.Storage.GetFolderSizeResponse;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.RenameFileRequest;
import com.flir.sdk.models.Storage.VolumeDetails;
import com.flir.sdk.models.Storage.VolumeResponse;
import com.flir.sdk.models.Storage.presignedUploadUrl;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class FolderPresenter {
    
    private final FolderView view;
    private CompositeDisposable subscriptions;
    private StorageInterceptor interceptor;


    public FolderPresenter(StorageInterceptor aStorageInterceptor, FolderView view) {
        this.view = view;
        this.interceptor = aStorageInterceptor;
        subscriptions = new CompositeDisposable();
    }

    public void getVolumeFiles(String volume, int pageSize) {
        Observable<VolumeFilesResponse> observable= interceptor.getVolumeFiles(volume, pageSize, null);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<VolumeFilesResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(VolumeFilesResponse response) {
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_SELECTED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, response.files.length + "");

                view.responseFromServer(response);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUMES_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_ITEM_SELECTED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, e.getMessage());

                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }

    public void getFile(String aFileId, int id){
        Observable<GetFileResponse> observable= interceptor.getFile(aFileId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetFileResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetFileResponse response) {
                view.updateRecyclerView(response);
                view.updateNotification(id, true);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.updateNotification(id, false);
                view.onFailure(e.getMessage());
            }

        }));

    }
    public void getFileForMqttEvent(String aFileId){
        Observable<GetFileResponse> observable= interceptor.getFile(aFileId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetFileResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetFileResponse response) {
                view.updateRecyclerView(response);
            }

            @Override
            public void onError(Throwable e) {
            }

        }));

    }


    public void presignedUploadUrl(File file, String aFilePath, String volume, String filename, String contentType) {
        Observable<presignedUploadUrl> observable= interceptor.presignedUploadUrl(volume, filename, contentType);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<presignedUploadUrl>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(presignedUploadUrl response) {
                view.uploadFileToCloud(response, aFilePath, file, true);

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_UPLOADED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, filename + "/" + contentType);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_UPLOADED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, filename + "/" + contentType + "/" + e.getMessage());
            }

        }));

    }

    public void uploadFile(RequestBody body, String url, String aFileId, String aContentType, int id) {

        Call<ResponseBody> call = interceptor.uploadFile(body, url, LambdaConstant.SERVER_AES256_ENCRYPTION,aContentType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                getFile(aFileId, id);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.removeWait();
                view.updateNotification(id, false);
            }
        });

    }

    public void presignedUpdateUrl(File file,String fileId, String aFilePath, String contentType) {
        Observable<presignedUploadUrl> observable= interceptor.presignedUpdateUrl(fileId, contentType);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<presignedUploadUrl>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(presignedUploadUrl response) {
                view.uploadFileToCloud(response, aFilePath, file, false);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));
    }

    public void updateFile(RequestBody body, String fileId, String uploadUrl, String encryption, String aContentType, int id, File aFile) {

        Call<ResponseBody> call = interceptor.updateFile(body,uploadUrl, encryption,aContentType);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                renameFile(fileId, aFile.getName(), false);
                view.updateNotification(id, true);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.removeWait();
                view.updateNotification(id, false);
            }
        });

    }

    public void presignedDeleteUrl(String aFileId) {
        Observable<presignedUploadUrl> observable= interceptor.presignedDeleteUrl(aFileId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<presignedUploadUrl>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(presignedUploadUrl response) {
                deleteFile(response.url, aFileId);
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_DELETED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, aFileId);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_DELETED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, aFileId + "/" + e.getMessage());
            }

        }));

        view.showWait();
    }

    public void deleteFile(String deleteUrl, String fileId) {

        Call<ResponseBody> call = interceptor.deleteFile(deleteUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.deleteFileSuccess(fileId);
                view.removeWait();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.removeWait();
            }
        });
        view.showWait();

    }

    public void renameFile(String fileId, String newName, boolean isShowWait) {
        RenameFileRequest renameFileRequest = new RenameFileRequest(newName);
        Observable<GetFileResponse> observable= interceptor.renameFile(renameFileRequest, fileId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetFileResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetFileResponse response) {
                view.updateRecyclerView(response);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_RENAMED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, fileId + "/" + newName);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_VOLUME_ITEM_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_VOLUME_FILE_RENAMED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, fileId + "/" + newName + "/" + e.getMessage());

            }

        }));

        if(isShowWait) {
            view.showWait();
        }
    }

    public void getVolumeSize(String volumeId) {
        Observable<GetFolderSizeResponse> observable= interceptor.getFolderSize(volumeId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetFolderSizeResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetFolderSizeResponse response) {
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }

    public void presignedDownloadUrl(String aFileId, String fileName, int id) {
        Observable<presignedUploadUrl> observable= interceptor.presignedDownloadUrl(aFileId);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<presignedUploadUrl>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(presignedUploadUrl response) {
                downloadFile(response.url, fileName, id);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

    }

    public void downloadFile(String downloadUrl, String fileName, int id) {

        Call<ResponseBody> call = interceptor.downloadFile(downloadUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                view.writeFileToDisk(response.body(),fileName, id);
                view.removeWait();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                view.removeWait();
            }
        });

    }

    public void onStop() {
        subscriptions.dispose();
    }


    public void createVolume(VolumeDetails volumeDetails) {
        Observable<VolumeResponse> observable= interceptor.createVolume(volumeDetails);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<VolumeResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(VolumeResponse response) {
                //Add relevant method when storage API will fix.
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }

    public void getVolumes() {
        Observable<List<VolumeResponse>> observable= interceptor.getVolumes();
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<VolumeResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<VolumeResponse> response) {
                //Add relevant method when storage API will fix.
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }
}
