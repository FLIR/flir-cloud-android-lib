package com.flir.sdk.NetworkTests;

import com.flir.sdk.Interceptors.UserInterceptor;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.UnitTestUtils;
import com.flir.sdk.models.userModel.ForgetPassword;
import com.flir.sdk.network.ServiceGenerator;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.schedulers.Schedulers;

import static org.awaitility.Awaitility.await;

/**
 * Created by Moti Amar on 03/04/2017.
 */

public class UserForgetPassword {

    private UserInterceptor userInterceptor;
    private boolean callReturnSuccessfully = false;

    @Before
    public void beforeTest() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        ServiceGenerator serviceGenerator = unitTestLoginInterceptor.getServiceGenerator();
        userInterceptor = new UserInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(),null);
    }

    @Test
    public void test_post_forget_password() throws InterruptedException {
        ForgetPassword forgetPassword = new ForgetPassword("mo11@mailinator.com");
        Completable completable = userInterceptor.postForgotPassword(forgetPassword);
        completable.subscribe(() -> {
            callReturnSuccessfully = true;
        }, UnitTestUtils::printOnErrorTestLog);

        await().atMost(10, TimeUnit.SECONDS).until(() -> callReturnSuccessfully);
    }

}
