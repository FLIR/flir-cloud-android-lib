package com.flir.sdk.NetworkTests;

import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.UnitTestUtils;
import com.flir.sdk.models.authenticationModel.Login;
import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.authenticationModel.SignUpResponse;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import java.util.concurrent.TimeUnit;
import io.reactivex.Observable;

import static org.awaitility.Awaitility.await;

/**
 * Created by Moti Amar on 16/03/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AuthenticationUnitTest {
    private static LoginResponse loginResponse;
    private LoginInterceptor loginInterceptor;
    private RefreshTokenResponse refreshTokenResponse;
    private SignUpResponse signUpResponse;

    @Before
    public void beforeTest() {
        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        loginInterceptor = unitTestLoginInterceptor.getLoginInterceptor();
    }

    @Test
    public void A_sign_up() throws InterruptedException {
        SignUp signUp = new SignUp();
        signUp.setFirstName("blo");
        signUp.setLastName("uld");
        signUp.setEmail(UnitTestUtils.getSaltString() + "@mailinator.com");
        signUp.setPassword("12345678");
        Observable observable = loginInterceptor.postSignUpRequest(signUp);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<SignUpResponse>() {
            @Override
            public void onNext(Object response) {
                signUpResponse = (SignUpResponse)response;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> signUpResponse != null);
    }

    @Test
    public void B_login() throws InterruptedException {
        Login login = new Login();
        login.setUsername("mo11@mailinator.com");
        login.setPassword("mailinator123");
        login.setExpirationDuration(600);
        Observable observable = loginInterceptor.postLoginRequest(login);

        observable.subscribeWith(new BaseDisposableObserverUnitTest<LoginResponse>() {
            @Override
            public void onNext(Object login) {
                loginResponse = (LoginResponse)login;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> loginResponse != null);
    }

    @Test
    public void C_refresh_token() throws InterruptedException {
        RefreshToken refreshToken = new RefreshToken(loginResponse.refreshToken);
        Observable observable = loginInterceptor.putRefreshToken(refreshToken);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<RefreshTokenResponse>() {
            @Override
            public void onNext(Object response) {
                refreshTokenResponse = (RefreshTokenResponse)response;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> refreshTokenResponse != null);
    }

}
