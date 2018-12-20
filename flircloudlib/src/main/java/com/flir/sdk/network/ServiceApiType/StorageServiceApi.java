package com.flir.sdk.network.ServiceApiType;

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

import java.util.List;

import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * Created by Moti on 15-May-17.
 */

public interface StorageServiceApi {

    @GET("/api/storage/files")
    Observable<VolumeFilesResponse> getVolumeFiles(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("volume") String volume, @Query("page_size") String pageSize, @Query("from") String from);

    @GET("/api/storage/files/{ticket}")
    Observable<VolumeFilesResponse> getFilesByTicket(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Path(value = "ticket", encoded = true) String ticket);

    @GET("/api/storage/files/{fileId}")
    Observable<GetFileResponse> getFile(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id,  @Path(value = "fileId", encoded = true) String fileId);

    @GET("/api/storage/size")
    Observable<GetFolderSizeResponse> getFolderSize(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query(value = "volume", encoded = true) String volume);

    @PUT("/api/storage/files/{fileId}/rename")
    Observable<GetFileResponse> renameFile(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id,@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Body RenameFileRequest renameFileRequest, @Path(value = "fileId", encoded = true) String fileId);

    @PUT("/api/storage/files/{fileId}/tags")
    Observable<GetFileResponse> updateFileTags(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Header(Constants.CONTENT_TYPE_KEY) String content_type, @Body UpdateFileTags updateFileTags, @Path(value = "fileId", encoded = true) String fileId);

    @GET("/api/storage/presignedUploadUrl")
    Observable<presignedUploadUrl> presignedUploadUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("volume") String volume, @Query("fileName") String filename, @Query("contentType") String contentType, @Query("tag") String... tag);

    @PUT
    Call<ResponseBody> uploadFile(@Url String url,
                                  @Body RequestBody file,
                                  @Header(Constants.X_AMZ_SERVER_SIDE_ENCRYPTION) String xAmzServerSideEncryption,
                                  @Header(Constants.CONTENT_TYPE_KEY) String content_type);


    @GET("/api/storage/presignedUpdateUrl")
    Observable<presignedUploadUrl> presignedUpdateUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query("fileId") String fileId, @Query("contentType") String contentType, @Query("tag") String... tag);

    @PUT
    Call<ResponseBody> updateFile(@Url String url,
                                        @Body RequestBody file,
                                        @Header(Constants.X_AMZ_SERVER_SIDE_ENCRYPTION) String xAmzServerSideEncryption,
                                        @Header(Constants.CONTENT_TYPE_KEY) String content_type);

    @PUT("/api/storage/volumes/{volume}")
    Observable<VolumeResponse> updateVolume(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Header(Constants.CONTENT_TYPE_KEY) String content_type, @Body UpdateVolume updateVolume, @Path(value = "volume", encoded = true) String volume);

    @GET("/api/storage/presignedDownloadUrl")
    Observable<presignedUploadUrl> presignedDownloadUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query(value = "fileId", encoded = true) String fileId);

    @GET
    @Streaming
    Call<ResponseBody> downloadFile(@Url String url);

    @GET("/api/storage/presignedDeleteUrl")
    Observable<presignedUploadUrl> presignedDeleteUrl(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Query(value = "fileId", encoded = true) String fileId);

    @DELETE
    Call<ResponseBody> deleteFile(@Url String url);


    @POST("/api/storage/volumes")
    Observable<VolumeResponse> createVolume(@Header(Constants.CONTENT_TYPE_KEY) String content_type, @Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id, @Body VolumeDetails volumeDetails);

    @GET("/api/storage/volumes")
    Observable<List<VolumeResponse>> getVolumes(@Header(Constants.X_ACCOUNT_ID_KEY) String x_account_id);


}
