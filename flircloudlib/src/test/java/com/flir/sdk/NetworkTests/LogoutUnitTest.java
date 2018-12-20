package com.flir.sdk.NetworkTests;

import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.AuthInterceptor;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.ShareElements;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.network.ServiceGenerator;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import static org.awaitility.Awaitility.await;

/**
 * Created by Moti Amar on 02/04/2017.
 */

public class LogoutUnitTest {
    private AuthInterceptor authInterceptor;
    private LoginInterceptor loginInterceptor;
    private static LoginResponse loginResponse;
    private ShareElements shareElements;
    private LogoutResponse response;
    private ServiceGenerator serviceGenerator;


    @Before
    public void beforeTest() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        loginInterceptor = unitTestLoginInterceptor.getLoginInterceptor();
        serviceGenerator = unitTestLoginInterceptor.getServiceGenerator();
        shareElements = unitTestLoginInterceptor.getShareElements();
    }

    @Test
    public void D_logout() throws InterruptedException {
        authInterceptor = new AuthInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
        RefreshToken refreshToken = new RefreshToken(shareElements.getRefreshToken());
        Observable observable = authInterceptor.putLogout(refreshToken);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<LogoutResponse>() {

            @Override
            public void onNext(Object logoutResponse) {
                response = (LogoutResponse)logoutResponse;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> response != null);
    }
}
