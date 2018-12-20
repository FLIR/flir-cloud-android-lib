package com.flir.sdk;

import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.Interceptors.LoginInterceptor;
import com.flir.sdk.models.authenticationModel.Login;
import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.network.ServiceGenerator;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

import static org.awaitility.Awaitility.await;

/**
 * Created by mamar on 12/4/2017.
 */

public class UnitTestLoginInterceptor {

    private static LoginResponse loginResponse;
    private ShareElements shareElements;
    private LoginInterceptor loginInterceptor;
    private AccountsInterceptor accountsInterceptor;
    private ServiceGenerator serviceGenerator;


    public UnitTestLoginInterceptor() {

        serviceGenerator = UnitTestServiceGenerator.getInstance().getServiceGenerator();
        loginInterceptor = new LoginInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io());

        doPostLoginRequest(serviceGenerator);
    }

    private void doPostLoginRequest(ServiceGenerator serviceGenerator) {
        Login login = new Login();
        login.setUsername("mo11@mailinator.com");
        login.setPassword("mailinator123");
        login.setExpirationDuration(600);

        Observable observable = loginInterceptor.postLoginRequest(login);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<LoginResponse>() {
            @Override
            public void onNext(Object response) {
                loginResponse = (LoginResponse)response;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> loginResponse != null);

        shareElements = new ShareElements(loginResponse.accessToken, loginResponse.typeAs, loginResponse.expiration, loginResponse.refreshToken, null, null);
        accountsInterceptor = new AccountsInterceptor(serviceGenerator, Schedulers.io(), Schedulers.io(), shareElements);
    }


    public  LoginResponse getLoginResponse() {
        return loginResponse;
    }

    public ShareElements getShareElements() {
        return shareElements;
    }

    public LoginInterceptor getLoginInterceptor() {
        return loginInterceptor;
    }

    public AccountsInterceptor getAccountsInterceptor() {
        return accountsInterceptor;
    }

    public ServiceGenerator getServiceGenerator() {
        return serviceGenerator;
    }



}
