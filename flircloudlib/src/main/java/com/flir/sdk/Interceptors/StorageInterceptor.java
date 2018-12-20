package com.flir.sdk.Interceptors;


import com.flir.sdk.Utils.Constants;
import com.flir.sdk.models.Storage.GetFileResponse;
import com.flir.sdk.models.Storage.GetFolderSizeResponse;
import com.flir.sdk.models.Storage.UpdateFileTags;
import com.flir.sdk.models.Storage.UpdateVolume;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.RenameFileRequest;
import com.flir.sdk.models.Storage.VolumeDetails;
import com.flir.sdk.models.Storage.VolumeResponse;
import com.flir.sdk.models.Storage.presignedUploadUrl;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.StorageServiceApi;
import com.flir.sdk.network.ServiceGenerator;
import com.google.gson.JsonObject;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by moti on 16/03/2017.
 */

public class StorageInterceptor {

    private StorageServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private AuthenticationProvider authenticationProvider;
    private ServiceGenerator serviceGenerator;

    public StorageInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        this.serviceGenerator = serviceGenerator;
        serviceApi = (StorageServiceApi) this.serviceGenerator.createService(StorageServiceApi.class, authenticationProvider);

        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
        this.authenticationProvider = authenticationProvider;
    }

    public Observable<VolumeFilesResponse> getVolumeFiles(String volume, int pageSize, String from) {
        return serviceApi.getVolumeFiles(authenticationProvider.getAccountToken() ,volume, String.valueOf(pageSize), from)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<VolumeFilesResponse> getVolumeFiles(String ticket) {
        return serviceApi.getFilesByTicket(authenticationProvider.getAccountToken() , ticket)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<VolumeResponse> updateVolume(UpdateVolume updateVolume, String serial) {
        return serviceApi.updateVolume(authenticationProvider.getAccountToken() ,Constants.CONTENT_TYPE, updateVolume, serial)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetFileResponse> getFile(String fileId) {
        return serviceApi.getFile(authenticationProvider.getAccountToken() ,fileId)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetFolderSizeResponse> getFolderSize(String volume) {
        return serviceApi.getFolderSize(authenticationProvider.getAccountToken() ,volume)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetFileResponse> renameFile(RenameFileRequest renameFileRequest, String fileId) {
        return serviceApi.renameFile(authenticationProvider.getAccountToken() , Constants.CONTENT_TYPE, renameFileRequest, fileId)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<GetFileResponse> updateFileTags(String fileId, UpdateFileTags updateFileTags) {
        return serviceApi.updateFileTags(authenticationProvider.getAccountToken() , Constants.CONTENT_TYPE, updateFileTags, fileId)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    //*String... keyValueTags* example: "folderName:Music", "type:mp3",...
    public Observable<presignedUploadUrl> presignedUploadUrl(String volume, String filename, String contentType, String... keyValueTags) {
        return serviceApi.presignedUploadUrl(authenticationProvider.getAccountToken(), volume, filename, contentType, keyValueTags)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Call<ResponseBody> uploadFile(RequestBody body, String uploadUrl, String encryption,String aContentType) {
        return serviceApi.uploadFile(uploadUrl , body, encryption, aContentType);
    }

    //*String... keyValueTags* example: "folderName:Music", "type:mp3",...
    public Observable<presignedUploadUrl> presignedUpdateUrl(String fileId,String contentType, String... keyValueTags) {
        return serviceApi.presignedUpdateUrl(authenticationProvider.getAccountToken(), fileId, contentType, keyValueTags)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Call<ResponseBody> updateFile(RequestBody body, String uploadUrl, String encryption,String aContentType) {
        return serviceApi.updateFile(uploadUrl, body, encryption,aContentType);
    }

    public Observable<presignedUploadUrl> presignedDownloadUrl(String fileId) {
        return serviceApi.presignedDownloadUrl(authenticationProvider.getAccountToken(), fileId)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }
    public Call<ResponseBody> downloadFile(String downloadUrl) {
        return serviceApi.downloadFile(downloadUrl);
    }

    public Observable<presignedUploadUrl> presignedDeleteUrl(String fileId) {
        return serviceApi.presignedDeleteUrl(authenticationProvider.getAccountToken(), fileId)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }
    public Call<ResponseBody> deleteFile(String deleteUrl) {
        return serviceApi.deleteFile(deleteUrl);
    }

    public Observable<VolumeResponse> createVolume(VolumeDetails volumeDetails) {
        return serviceApi.createVolume(Constants.CONTENT_TYPE, authenticationProvider.getAccountToken(), volumeDetails)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }

    public Observable<List<VolumeResponse>> getVolumes() {
        return serviceApi.getVolumes(authenticationProvider.getAccountToken())
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler);
    }



}
