package com.flir.sdk.NetworkTests;

import com.flir.sdk.BaseDisposableObserverUnitTest;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.UnitTestLoginInterceptor;
import com.flir.sdk.UnitTestUtils;
import com.flir.sdk.models.accounts.CreateAccount;
import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;
import com.google.gson.JsonObject;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

import static org.awaitility.Awaitility.await;

/**
 * Created by Moti Amar on 19/04/2017.
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AccountsUnitTest {

   private AccountsInterceptor accountsInterceptor;
    private CreateAccountResponse mCreateAccountResponse;
    private GetAccountResponse accountResponse;
    private CreateAccountResponse updateAccountResponse;

    @Before
    public void login() throws InterruptedException {

        UnitTestLoginInterceptor unitTestLoginInterceptor = new UnitTestLoginInterceptor();
        accountsInterceptor = unitTestLoginInterceptor.getAccountsInterceptor();
    }

    @Test
    public void test_A_post_Create_Account() throws InterruptedException {

        JsonObject info = new JsonObject();
        info.addProperty("url","some url");
        CreateAccount createAccount = new CreateAccount(UnitTestUtils.createRandomString(), info);
        Observable observable = accountsInterceptor.postCreateAccount(createAccount);

        observable.subscribeWith(new BaseDisposableObserverUnitTest<CreateAccountResponse>() {
            @Override
            public void onNext(Object response) {
                mCreateAccountResponse = (CreateAccountResponse) response;
            }
        });

        await().atMost(10, TimeUnit.SECONDS).until(() -> mCreateAccountResponse != null);
    }

    @Test
    public void test_B_get_Account_By_Type() throws InterruptedException {
        Observable observable = accountsInterceptor.getAccountByType("flir");
        observable.subscribeWith(new BaseDisposableObserverUnitTest<GetAccountResponse>() {
            @Override
            public void onNext(Object response) {
                accountResponse = (GetAccountResponse)response;
            }
        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> accountResponse != null);
    }

    @Test
    public void test_C_put_Update_Account() throws InterruptedException {

        JsonObject info = new JsonObject();
        info.addProperty("url","Some other Url");
        CreateAccount createAccount = new CreateAccount("flir",info);
        Observable observable = accountsInterceptor.putUpdateAccount(createAccount);
        observable.subscribeWith(new BaseDisposableObserverUnitTest<CreateAccountResponse>() {
            @Override
            public void onNext(Object response) {
                updateAccountResponse = (CreateAccountResponse)response;
            }

        });
        await().atMost(10, TimeUnit.SECONDS).until(() -> updateAccountResponse != null);
    }

}
