package com.flir.sdk.NetworkTests;

import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.ShareElements;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.UnitTestUtils;
import com.flir.sdk.models.userModel.ChangePassword;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.userModel.UpdateUserResponse;
import com.flir.sdk.models.userModel.UserPicture;
import com.flir.sdk.network.ServiceGenerator;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import static org.awaitility.Awaitility.await;

/**
 * Created by Moti Amar on 19/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserUnitTest {
    private LoginInterceptor loginInterceptor;
    private ShareElements shareElements;
    private UserInterceptor userInterceptor;
    private UpdateUserResponse updateUserResponse;
    private UpdateUserResponse putUpdateUser;
    private boolean callReturnSuccessfully = false;
    private UserPicture userPicture;


    @Before
    public void login() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        ServiceGenerator serviceGenerator = unitTestLoginInterceptor.getServiceGenerator();
        shareElements = unitTestLoginInterceptor.getShareElements();
        userInterceptor = new UserInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
    }


    @Test
    public void A_test_put_update_user() throws InterruptedException {
        SignUp signUp = new SignUp();
        signUp.setFirstName("test");
        signUp.setLastName("unit");
        signUp.setEmail("unittest@mailinator.com");
        signUp.setPassword("mailinator123");
        Observable observable = userInterceptor.putUpdateUser(signUp);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<UpdateUserResponse>() {
            @Override
            public void onNext(Object response) {
                putUpdateUser = (UpdateUserResponse)response;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> (putUpdateUser != null));
    }

    @Test
    public void B_test_get_user_picture() throws InterruptedException {
        Observable observable = userInterceptor.getUserPicture();
        observable.subscribeWith(new BaseDisposableObserverUnitTest<UserPicture>() {
            @Override
            public void onNext(Object response) {
                userPicture = (UserPicture)response;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> (userPicture != null && userPicture.url != null));
    }

    @Test
    public void C_test_get_self_user() throws InterruptedException {
        Observable observable = userInterceptor.getSelfUser();
        observable.subscribeWith(new BaseDisposableObserverUnitTest<UpdateUserResponse>() {
            @Override
            public void onNext(Object response) {
                updateUserResponse = (UpdateUserResponse)response;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> updateUserResponse != null);
    }


    @Test
    public void A_test_post_upload_user_profile_picture() throws InterruptedException {
        //Set a photo as a profile.jpg to internal store.
        File file = new File(UnitTestUtils.resolveBasePath());
        InputStream in = null;
        byte[] buf = new byte[0];
        try {
            in = new FileInputStream(file);
            buf = new byte[in.available()];
            while (in.read(buf) != -1) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (buf.length != 0) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), buf);
            Completable completable = userInterceptor.postUploadUserProfilePicture(requestBody);
            completable.subscribe(() -> {
                callReturnSuccessfully = true;
            }, UnitTestUtils::printOnErrorTestLog);
        }
        await().atMost(10, TimeUnit.SECONDS).until(() -> callReturnSuccessfully);
    }

    @Test
    public void D_test_put_change_password() throws InterruptedException {
        ChangePassword changePassword = new ChangePassword("mailinator123", "mailinator123");
        Completable completable = userInterceptor.putChangePassword(changePassword);
        completable.subscribe(() -> {
            callReturnSuccessfully = true;
        }, UnitTestUtils::printOnErrorTestLog);
        await().atMost(10, TimeUnit.SECONDS).until(() -> callReturnSuccessfully);
    }



}
