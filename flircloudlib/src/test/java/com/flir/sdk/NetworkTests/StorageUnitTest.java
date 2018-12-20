package com.flir.sdk.NetworkTests;


import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.Interceptors.StorageInterceptor;
import com.flir.sdk.ShareElements;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.models.Storage.GetFileResponse;
import com.flir.sdk.models.Storage.GetFolderSizeResponse;
import com.flir.sdk.models.Storage.VolumeFilesResponse;
import com.flir.sdk.models.Storage.RenameFileRequest;
import com.flir.sdk.models.Storage.VolumeDetails;
import com.flir.sdk.models.Storage.VolumeResponse;
import com.flir.sdk.models.Storage.presignedUploadUrl;
import com.flir.sdk.models.accounts.GetAccountResponse;
import com.flir.sdk.network.ServiceGenerator;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static org.awaitility.Awaitility.await;

/**
 * Created by Moti Amar on 19/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class StorageUnitTest {

    public static final String CONTENT_TYPE = "video/mp4";
    private  StorageInterceptor mStorageInterceptor;
    private ShareElements shareElements;
    private AccountsInterceptor mAccountsInterceptor;
    private static VolumeResponse mVolumeResponse;
    private VolumeFilesResponse mGetUserFilesResponse;
    private GetFileResponse mGetFileResponse;
    private GetFolderSizeResponse mGetFolderSizeResponse;
    private GetFileResponse aRenameFileResponse;
    private static presignedUploadUrl mPresignedUploadUrl;
    private static presignedUploadUrl mPresignedDownloadUrl;
    private static presignedUploadUrl mPresignedUpdateUrl;
    private static presignedUploadUrl mPresignedDeleteUrl;
    private Response<ResponseBody> mResponse;
    private Response<ResponseBody> mDownloadFileSuccessedResponse;
    private Response<ResponseBody> mDeleteFileSuccessedResponse;
    private String accountToken;
    private static String mRandomVolumeName;
    private boolean getVolumes;

    @Before
    public void A_login() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        ServiceGenerator serviceGenerator = unitTestLoginInterceptor.getServiceGenerator();
        shareElements = unitTestLoginInterceptor.getShareElements();
        mAccountsInterceptor = unitTestLoginInterceptor.getAccountsInterceptor();

        Observable observable = mAccountsInterceptor.getAccountByType("flir");
        observable.subscribeWith(new BaseDisposableObserverUnitTest<GetAccountResponse>() {
            @Override
            public void onNext(Object response) {
                accountToken = ((GetAccountResponse)response).accountToken;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> accountToken != null);
        shareElements.setAccountToken(accountToken);
        mStorageInterceptor = new StorageInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
    }

    /*createNewVolume*/
    @Test
    public void test1() throws InterruptedException {

        VolumeDetails volumeDetails = new VolumeDetails(createRandomString());
        Observable observable = mStorageInterceptor.createVolume(volumeDetails);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<VolumeResponse>() {


            @Override
            public void onNext(Object aVolumeResponse) {
                mVolumeResponse = (VolumeResponse)aVolumeResponse;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mVolumeResponse != null);
    }

    /*getVolumes*/
    @Test
    public void test2() throws InterruptedException {

        Observable observable = mStorageInterceptor.getVolumes();
        observable.subscribeWith(new BaseDisposableObserverUnitTest<List<VolumeResponse>>() {

            @Override
            public void onNext(Object aVolumeResponse) {
                getVolumes = true;
                //mListVolumeResponse = (List<VolumeResponse>)aVolumeResponse;dfdsf
            }


        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> getVolumes);
    }

    /*getFolderSize*/
    @Test
    public void test3() throws InterruptedException {

        Observable observable = mStorageInterceptor.getFolderSize(mVolumeResponse.VolumeId);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<GetFolderSizeResponse>() {

            @Override
            public void onNext(Object aGetFolderSizeResponse) {
                mGetFolderSizeResponse = (GetFolderSizeResponse)aGetFolderSizeResponse;
            }


        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mGetFolderSizeResponse != null);
    }



    /*presignedUploadUrl*/
    @Test
    public void test4() throws InterruptedException {

        Observable observable = mStorageInterceptor.presignedUploadUrl(mVolumeResponse.VolumeId,"fileNameTest", CONTENT_TYPE, "folderName:Music", "type:mp3");
        observable.subscribeWith(new BaseDisposableObserverUnitTest<presignedUploadUrl>() {

            @Override
            public void onNext(Object aPresignedUploadUrl) {
                mPresignedUploadUrl = (presignedUploadUrl)aPresignedUploadUrl;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mPresignedUploadUrl != null);
    }

    /*uploadFile*/
    @Test
    public void test5() throws InterruptedException {

        URL url  = this.getClass().getClassLoader().getResource("json.txt");

        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = MultipartBody.create(MediaType.parse(CONTENT_TYPE),file);
        Call<ResponseBody> call = mStorageInterceptor.uploadFile(requestBody, mPresignedUploadUrl.url,"AES256", CONTENT_TYPE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mResponse = response;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mResponse != null);
    }




    /*presignedUpdateUrl*/
    @Test
    public void test6() throws InterruptedException {

        Observable observable = mStorageInterceptor.presignedUpdateUrl(mPresignedUploadUrl.fileId  ,CONTENT_TYPE, "folderName:Music2", "type:mp4");
        observable.subscribeWith(new BaseDisposableObserverUnitTest<presignedUploadUrl>() {


            @Override
            public void onNext(Object aPresignedUploadUrl) {
                mPresignedUpdateUrl = (presignedUploadUrl)aPresignedUploadUrl;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mPresignedUpdateUrl != null);
    }

    /*updateFile*/
    @Test
    public void test7() throws InterruptedException {

        URL url  = this.getClass().getClassLoader().getResource("json.txt");

        File file = null;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        RequestBody requestBody = MultipartBody.create(MediaType.parse(CONTENT_TYPE),file);
        Call<ResponseBody> call = mStorageInterceptor.updateFile(requestBody, mPresignedUpdateUrl.url,"AES256", CONTENT_TYPE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mResponse = response;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> mResponse != null);
    }

    /*presignedDownloadUrl*/
    @Test
    public void test8() throws InterruptedException {

        Observable observable = mStorageInterceptor.presignedDownloadUrl(mPresignedUploadUrl.fileId);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<presignedUploadUrl>() {

            @Override
            public void onNext(Object aPresignedUploadUrl) {
                mPresignedDownloadUrl = (presignedUploadUrl)aPresignedUploadUrl;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mPresignedDownloadUrl != null);
    }

    /*DownloadFile*/
    @Test
    public void test9() throws InterruptedException {


        Call<ResponseBody> call = mStorageInterceptor.downloadFile(mPresignedDownloadUrl.url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mDownloadFileSuccessedResponse = response;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mDownloadFileSuccessedResponse != null);
    }

    /*getVolumeFiles*/
    @Test
    public void test_10() throws InterruptedException {

        Observable observable = mStorageInterceptor.getVolumeFiles(mVolumeResponse.VolumeId,5 , null);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<VolumeFilesResponse>() {
            @Override
            public void onNext(Object aGetUserFilesResponse) {
                mGetUserFilesResponse = (VolumeFilesResponse)aGetUserFilesResponse;
            }

        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> mGetUserFilesResponse != null);
    }

    /*getFileDetails*/
    @Test
    public void test_11() throws InterruptedException {

        Observable observable = mStorageInterceptor.getFile(mPresignedUploadUrl.fileId);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<GetFileResponse>() {

            @Override
            public void onNext(Object aGetFileResponse) {
                mGetFileResponse = (GetFileResponse)aGetFileResponse;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mGetFileResponse != null);
    }



    /*renameFile*/
    @Test
    public void test_12() throws InterruptedException {

        RenameFileRequest renameFileRequest = new RenameFileRequest("newFileNAme");
        Observable observable = mStorageInterceptor.renameFile(renameFileRequest, mPresignedUploadUrl.fileId);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<GetFileResponse>() {
            @Override
            public void onNext(Object aGetFileResponse) {
                aRenameFileResponse = (GetFileResponse)aGetFileResponse;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> aRenameFileResponse != null);
    }



    /*presignedDeleteUrl*/
    @Test
    public void test_13() throws InterruptedException {

        Observable observable = mStorageInterceptor.presignedDeleteUrl(mPresignedUploadUrl.fileId);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<presignedUploadUrl>() {

            @Override
            public void onNext(Object aPresignedUploadUrl) {
                mPresignedDeleteUrl = (presignedUploadUrl)aPresignedUploadUrl;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mPresignedDeleteUrl != null);
    }

    /*deleteFile*/
    @Test
    public void test_14() throws InterruptedException {

        Call<ResponseBody> call = mStorageInterceptor.deleteFile(mPresignedDeleteUrl.url);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                mDeleteFileSuccessedResponse = response;
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> mDeleteFileSuccessedResponse != null);
    }


    private static String createRandomString(){
        if(mRandomVolumeName == null) {
            mRandomVolumeName = UUID.randomUUID().toString();
        }
        return mRandomVolumeName;
    }

}
