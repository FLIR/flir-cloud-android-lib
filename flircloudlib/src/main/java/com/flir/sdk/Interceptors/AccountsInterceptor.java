package com.flir.sdk.Interceptors;

import com.flir.sdk.models.accounts.Account;
import com.flir.sdk.models.accounts.CreateAccount;
import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;
import com.flir.sdk.network.AuthenticationProvider;
import com.flir.sdk.network.ServiceApiType.AccountServiceApi;
import com.flir.sdk.network.ServiceGenerator;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Scheduler;
import io.reactivex.functions.Function;

/**
 * Created by Moti Amar on 19/04/2017.
 */

public class AccountsInterceptor {
    private AccountServiceApi serviceApi;
    private Scheduler subscribeOnScheduler;
    private Scheduler observeOnScheduler;
    private final String CONTENT_TYPE = "application/json";

    public AccountsInterceptor(ServiceGenerator serviceGenerator, Scheduler subscribeOnScheduler, Scheduler observeOnScheduler, AuthenticationProvider authenticationProvider) {
        serviceApi = (AccountServiceApi) serviceGenerator.createService(AccountServiceApi.class, authenticationProvider);
        this.subscribeOnScheduler = subscribeOnScheduler;
        this.observeOnScheduler = observeOnScheduler;
    }
    public Observable postCreateAccount(CreateAccount createAccount) {
        return serviceApi.createAccount(CONTENT_TYPE, createAccount)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends CreateAccountResponse>>) Observable::error);
    }
    public Observable getAccountByType(String accountType) {
        return serviceApi.getAccountByType(CONTENT_TYPE, accountType)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends GetAccountResponse>>) Observable::error);
    }
    public Observable<List<CreateAccountResponse>> getAllAccounts() {
        return serviceApi.getAllAccounts(CONTENT_TYPE)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends List<CreateAccountResponse>>>) Observable::error);
    }
    public Observable putUpdateAccount(CreateAccount createAccount) {
        return serviceApi.updateAccount(CONTENT_TYPE, createAccount)
                .subscribeOn(subscribeOnScheduler)
                .observeOn(observeOnScheduler)
                .onErrorResumeNext((Function<Throwable, ObservableSource<? extends CreateAccountResponse>>) Observable::error);
    }

}
