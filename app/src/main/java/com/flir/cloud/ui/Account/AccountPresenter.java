package com.flir.cloud.ui.Account;

import com.flir.cloud.DialogManagerFiles.IPresenter;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.models.accounts.CreateAccount;
import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;

/**
 * Created by Moti Amar on 20/04/2017.
 */

public class AccountPresenter implements IPresenter {

    public static String GET_ACCOUNT_BY_TYPE = "getAccountByType";
    private final AccountView view;
    private CompositeDisposable subscriptions;
    private AccountsInterceptor interactor;

    public AccountPresenter(AccountsInterceptor interactor, AccountView view) {
        this.view = view;
        this.interactor = interactor;
        subscriptions = new CompositeDisposable();
    }


    public void postCreateAccount(CreateAccount createAccount) {
        Observable<CreateAccountResponse> observable = interactor.postCreateAccount(createAccount);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<CreateAccountResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(CreateAccountResponse response) {
                view.responseFromServer(response);
               // view.removeWait();
                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_CREATE_NEW_ACCOUNT_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, createAccount.accountType);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_CREATE_NEW_ACCOUNT_CLICKED,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, createAccount.accountType);
            }

        }));

        view.showWait();
    }

    public void putUpdateAccount(CreateAccount createAccount) {
        Observable<CreateAccountResponse> observable = interactor.putUpdateAccount(createAccount);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<CreateAccountResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(CreateAccountResponse response) {
                view.responseFromServer(response);
                view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }
//
    public void getAccountByType(String accountName) {
        Observable<GetAccountResponse> observable = interactor.getAccountByType(accountName);
        subscriptions.add(observable.subscribeWith(new DisposableObserver<GetAccountResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(GetAccountResponse response) {
                view.responseFromServer(response);
                view.removeWait();

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ACCOUNT_SELECTION_MADE,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SUCCESS_RESPONSE, accountName);
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage(), GET_ACCOUNT_BY_TYPE);

                view.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ACCOUNT_SELECTION_MADE,
                        LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_FAILURE_RESPONSE, accountName + "  Error Message = " + e.getMessage());
            }

        }));

        view.showWait();
    }

    public void getAllAccounts() {
        Observable<List<CreateAccountResponse>> observable = interactor.getAllAccounts();
        subscriptions.add(observable.subscribeWith(new DisposableObserver<List<CreateAccountResponse>>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(List<CreateAccountResponse> response) {
                view.responseFromServer(response);
              //  view.removeWait();
            }

            @Override
            public void onError(Throwable e) {
                view.removeWait();
                view.onFailure(e.getMessage());
            }

        }));

        view.showWait();
    }
    public void onStop() {
        subscriptions.dispose();
    }
}
