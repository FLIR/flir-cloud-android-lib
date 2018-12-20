package com.flir.sdk.NetworkTests;

import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.AccessInterceptor;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.ShareElements;
import com.flir.sdk.TestConstant;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.UnitTestUtils;
import com.flir.sdk.models.Access.AddResource;
import com.flir.sdk.models.Access.ResourceResponse;
import com.flir.sdk.models.Access.ShareResource;
import com.flir.sdk.models.Access.SharedUsersResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;
import com.flir.sdk.network.ServiceGenerator;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;

import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by moti on 02/04/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccessUnitTest {
    private AccessInterceptor aclInterceptor;
    private ShareElements shareElements;
    private AccountsInterceptor mAccountsInterceptor;
    private static ResourceResponse deviceResponse;
    private ResourceResponse updateDeviceResponse;
    private String accountToken;
    private static String randomName = UnitTestUtils.createRandomString();
    private boolean deleteDeviceSuccess;
    private boolean shareDeviceSuccess;
    private boolean getAllDeviceSuccess;
    private boolean unShareDeviceSuccess;
    private boolean getAllShareDeviceSuccess;
    private ServiceGenerator mServiceGenerator;


    @Before
    public void beforeTest() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        mAccountsInterceptor = unitTestLoginInterceptor.getAccountsInterceptor();
        shareElements = unitTestLoginInterceptor.getShareElements();
        mServiceGenerator = unitTestLoginInterceptor.getServiceGenerator();

        Observable observable = mAccountsInterceptor.getAccountByType("flir");
        observable.subscribeWith(new BaseDisposableObserverUnitTest<GetAccountResponse>() {
            @Override
            public void onNext(Object response) {
                accountToken = ((GetAccountResponse)response).accountToken;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> accountToken != null);
        shareElements.setAccountToken(accountToken);
        aclInterceptor = new AccessInterceptor(mServiceGenerator, Schedulers.io(), Schedulers.io(), shareElements);

    }

    @Test
    public void Test_1_Add_device() throws InterruptedException {

        JsonObject tags = new JsonObject();
        AddResource AddResource = new AddResource(randomName, TestConstant.RESOURCE_TYPE_DEVICE, "flir_name", tags);

        Observable observable = aclInterceptor.postAddResource(AddResource);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<ResourceResponse>() {
            @Override
            public void onNext(Object response) {
                deviceResponse = (ResourceResponse)response;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> deviceResponse != null);
    }


    @Test
    public void Test_2_Get_device() throws InterruptedException {

        Observable observable = aclInterceptor.getGetSingleResource(randomName);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<ResourceResponse>() {

            @Override
            public void onNext(Object response) {
                deviceResponse = (ResourceResponse)response;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> deviceResponse != null);
    }

    @Test
    public void Test_3_Get_all_devices() throws InterruptedException {
        aclInterceptor = new AccessInterceptor(mServiceGenerator, Schedulers.io(), Schedulers.io(), shareElements);

        Observable observable = aclInterceptor.getAllResources();
        observable.subscribeWith(new BaseDisposableObserverUnitTest<List<ResourceResponse>>() {
            @Override
            public void onNext(Object response) {
                getAllDeviceSuccess = true;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> getAllDeviceSuccess);
    }

    @Test
    public void Test_4_update_device() throws InterruptedException {
        aclInterceptor = new AccessInterceptor(mServiceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
        JsonObject tags = new JsonObject();
        AddResource updateDevice = new AddResource(randomName, TestConstant.RESOURCE_TYPE_DEVICE, "flir_name2", tags);
        Observable observable = aclInterceptor.putUpdateResource(updateDevice);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<ResourceResponse>() {
            @Override
            public void onNext(Object response) {
                updateDeviceResponse = (ResourceResponse)response;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> updateDeviceResponse != null);
    }


    @Test
    public void Test_6_share_device() throws InterruptedException {
        //Created other account with those details:
        // Email: unit_test@mailinator.com
        // Password: 907580
        String otherAccountEmail = "mot1@mailinator.com";
        //Create a new device to share:
       // createDeviceForTests();
        if(deviceResponse != null && !deviceResponse.id.isEmpty()) {
            aclInterceptor = new AccessInterceptor(mServiceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
            ShareResource sd = new ShareResource(deviceResponse.id, otherAccountEmail);
            Observable observable = aclInterceptor.postShareResource(sd);
            observable.subscribeWith(new BaseDisposableObserverUnitTest<ResponseBody>() {

                @Override
                public void onNext(Object response) {
                    shareDeviceSuccess = true;
                }
            });

            await().atMost(10, TimeUnit.SECONDS).until(() -> shareDeviceSuccess);
        }else{
            assertTrue(false);
        }
    }

    @Test
    public void Test_7_get_shared_devices() throws InterruptedException {

        if(deviceResponse != null && !deviceResponse.id.isEmpty()) {
            aclInterceptor = new AccessInterceptor(mServiceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
            Observable observable = aclInterceptor.getSharedResources(deviceResponse.id);
            observable.subscribeWith(new BaseDisposableObserverUnitTest<List<SharedUsersResponse>>() {
                @Override
                public void onNext(Object listDeviceRes) {
                    getAllShareDeviceSuccess = true;
                }
            });

            await().atMost(10, TimeUnit.SECONDS).until(() -> getAllShareDeviceSuccess);
        }else{
            assertTrue(false);
        }

    }

    @Test
    public void Test_8_un_share_device() throws InterruptedException {
        //Created other account with those details:
        // Email: unit_test@mailinator.com
        // Password: 907580
        String otherAccountEmail = "mot1@mailinator.com";
        //Create a new device to share:
      //  createDeviceForTests();
      //  if(testDeviceResponse != null && !testDeviceResponse.id.isEmpty()) {
        if(deviceResponse != null && !deviceResponse.id.isEmpty()) {
            ShareResource sd = new ShareResource(deviceResponse.id, otherAccountEmail);
            Observable<ResponseBody> observable = aclInterceptor.postUnShareResource(sd);
            observable.subscribeWith(new BaseDisposableObserverUnitTest<ResponseBody>() {

                @Override
                public void onNext(Object response) {
                    unShareDeviceSuccess = true;
                }

            });

            await().atMost(10, TimeUnit.SECONDS).until(() -> unShareDeviceSuccess);
        }else{
            assertTrue(false);
        }

    }

    @Test
    public void Test_9_delete_device() throws InterruptedException {
        aclInterceptor = new AccessInterceptor(mServiceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
        Observable<ResponseBody> observable = aclInterceptor.deleteResource(deviceResponse.id);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<ResponseBody>() {
            @Override
            public void onNext(Object response) {
                deleteDeviceSuccess = true;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> deleteDeviceSuccess);
    }

}
