package com.flir.sdk.NetworkTests;

import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.Interceptors.DeviceInterceptor;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.ShareElements;
import com.flir.sdk.TestConstant;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.UnitTestUtils;
import com.flir.sdk.models.Access.AddResource;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Device.GetDeviceStateResponse;
import com.flir.sdk.models.Device.UpdateReported;
import com.flir.sdk.models.Device.UpdateState;
import com.flir.sdk.models.Device.UploadDownloadUrlResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;
import com.flir.sdk.network.ServiceGenerator;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static org.awaitility.Awaitility.await;

/**
 * Created by moti on 02/04/2017.
 */
public class DeviceUnitTest {
    public static final String THUMBNAIL = "thumbnail";
    public static final String CAMERA_NAME_MOTI_CAM = "moti-cam-3";
    private AccessInterceptor accessInterceptor;
    private DeviceInterceptor deviceInterceptor;
    private LoginInterceptor loginInterceptor;
    private ShareElements shareElements;
    private AccountsInterceptor mAccountsInterceptor;
    private ResourceResponse deviceResponse;
    private String accountToken;
    private static String randomName;
    private boolean deviceStateResponse;
    private boolean updateDesired;
    private boolean updateReported;
    private boolean uploadURL;
    private boolean downloadFileURL;

    @Before
    public void beforeTest() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        loginInterceptor = unitTestLoginInterceptor.getLoginInterceptor();
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

        accessInterceptor = new AccessInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
        deviceInterceptor = new DeviceInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
        randomName = UnitTestUtils.createRandomString();
        JsonObject tags = new JsonObject();

        AddResource addDevice = new AddResource(randomName, TestConstant.RESOURCE_TYPE_DEVICE, "flir_name2", tags);

        Observable observable3 = accessInterceptor.postAddResource(addDevice);
        observable3.subscribeWith(new BaseDisposableObserverUnitTest<ResourceResponse>() {

            @Override
            public void onNext(Object response) {
                deviceResponse = (ResourceResponse)response;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> deviceResponse != null);

    }

    @Test
    public void getDeviceState() throws InterruptedException {

        Observable observable2 = deviceInterceptor.getDeviceState(randomName,  null);
        observable2.subscribeWith(new BaseDisposableObserverUnitTest<List<GetDeviceStateResponse>>() {
        @Override
            public void onNext(Object response) {
            deviceStateResponse = true;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> deviceStateResponse);
    }

    @Test
    public void UpdateDesired() throws InterruptedException {

        UpdateState ud = new UpdateState();
        List<UpdateState> updateDesiredList = new ArrayList<>();
        ud.setKey("sdCardRecording");
        ud.setValue("false");
        updateDesiredList.add(ud);
        Observable observable2 = deviceInterceptor.postUpdateDesired(randomName, updateDesiredList);
        observable2.subscribeWith(new BaseDisposableObserverUnitTest<ResponseBody>() {
            @Override
            public void onNext(Object response) {
                updateDesired = true;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> updateDesired);
    }

    @Test
    public void UpdateReported() throws InterruptedException {

        UpdateReported ud = new UpdateReported();
        List<UpdateReported> UpdateReportedList = new ArrayList<>();
        ud.setKey("sdCardRecording");
        ud.setValue("false");
        UpdateReportedList.add(ud);
        Observable observable2 = deviceInterceptor.postUpdateReported(randomName, UpdateReportedList, false);
        observable2.subscribeWith(new BaseDisposableObserverUnitTest<ResponseBody>() {
            @Override
            public void onNext(Object response) {
                updateReported = true;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> updateReported);
    }

    @Test
    public void getUploadURL() throws InterruptedException {

        Observable observable = deviceInterceptor.getPresignedUploadUrl(randomName, THUMBNAIL,null);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<UploadDownloadUrlResponse>() {

            @Override
            public void onNext(Object response) {
                uploadURL = true;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> uploadURL);
    }

    @Test
    public void getDownloadFileURL() throws InterruptedException {
        //Signin with an account that has a device live.
        Observable observable = deviceInterceptor.getDownloadFile(CAMERA_NAME_MOTI_CAM, THUMBNAIL, null);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<UploadDownloadUrlResponse>() {
            @Override
            public void onNext(Object response) {
                downloadFileURL = true;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> downloadFileURL);
    }

}
