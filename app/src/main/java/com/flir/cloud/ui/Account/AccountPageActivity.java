package com.flir.cloud.ui.Account;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListAdapter;
import com.afollestad.materialdialogs.simplelist.MaterialSimpleListItem;
import com.flir.cloud.EventManager.LambdaAnalyticsEventManager;
import com.flir.cloud.LambdaConstant;
import com.flir.cloud.MainApplication;
import com.flir.cloud.R;
import com.flir.cloud.ui.Access.AccessDevicesCarouselActivity;
import com.flir.cloud.ui.Authentication.Login.LoginActivity;
import com.flir.cloud.ui.Views.LambdaCustomProgressBar;
import com.flir.sdk.Interceptors.AccountsInterceptor;
import com.flir.sdk.Interceptors.AuthInterceptor;
import com.flir.sdk.models.accounts.CreateAccount;
import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;
import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.network.AuthenticationProvider;
import com.google.gson.JsonObject;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.observers.DisposableObserver;
import retrofit2.HttpException;

/**
 * Created by Moti on 29-May-17.
 */

public class AccountPageActivity extends AppCompatActivity implements AccountView {

    public static final String CREATE_ACCOUNT_TAG = "createNewAccount";
    public static final String ACCOUNT_TAG_ZERO_RESPONSE = "zeroResponse";
    private MaterialSimpleListAdapter mAdapter;
    private MaterialDialog mMaterialDialog;
    private boolean doForceCrateAccount;

    AccountPresenter presenter;
    @Inject
    AccountsInterceptor accountsInteractor;

    @Inject
    AuthenticationProvider authenticationProvider;

    private LambdaAnalyticsEventManager mLambdaAnalyticsEventManager;

    @Inject
    AuthInterceptor authInteractor;

    @BindView(R.id.lambda_custom_progress_bar_account)
    LambdaCustomProgressBar progressBar;

    @OnClick(R.id.account_chooser_dialog)
    public void chooseAccountClicked() {
        chooseAccountClickedListener();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);
        ButterKnife.bind(this);
        ((MainApplication) getApplication()).getApplicationComponent().inject(this);
        mLambdaAnalyticsEventManager = new LambdaAnalyticsEventManager(this);
        presenter = new AccountPresenter(accountsInteractor, this);
        doForceCrateAccount = getIntent().getBooleanExtra(LambdaConstant.FORCE_CREATE_ACCOUNT, false);
        progressBar.setVisibility(View.VISIBLE);
        presenter.getAllAccounts();
    }


    @Override
    public void showWait() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void removeWait() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onFailure(String appErrorMessage) {

    }

    @Override
    public void onFailure(String appErrorMessage, String myMessage) {

    }

    @Override
    public void responseFromServer(CreateAccountResponse response) {
        presenter.getAccountByType(response.accountType);
    }

    @Override
    public void responseFromServer(GetAccountResponse response) {
        authenticationProvider.updateStorageAccountTokenAndID(response.accountToken, response.id);
        openProfilePage();
    }

    @Override
    public void responseFromServer(List<CreateAccountResponse> response) {
        openAccountChooserDialog(response);
    }

    @Override
    public void sendEvent(String aCategory, String aAction, String aEvent, String aComment) {
        mLambdaAnalyticsEventManager.sendEvent(aCategory, aAction, aEvent, aComment);
    }

    private void openAccountChooserDialog(List<CreateAccountResponse> response) {

        if (response.size() == 1 && !doForceCrateAccount) {
            showWait();
            openProfilePageWithExistingAccount(response.get(0).accountType);

        } else {
            removeWait();
            mAdapter = new MaterialSimpleListAdapter(new MaterialSimpleListAdapter.Callback() {
                @Override
                public void onMaterialListItemSelected(MaterialDialog dialog, int index, MaterialSimpleListItem item) {
                    onChooseAccountListItemSelected(item);
                }
            });

            for (int i = 0; i < response.size(); i++) {

                mAdapter.add(new MaterialSimpleListItem.Builder(this)
                        .content(response.get(i).accountType)
                        .icon(R.drawable.ic_account_icon)
                        .backgroundColor(Color.WHITE)
                        .build());
            }

            if (response.size() == 0) {

                mAdapter.add(new MaterialSimpleListItem.Builder(this)
                        .content(LambdaConstant.ACCOUNT_TYPE_FLIR)
                        .icon(R.drawable.ic_account_icon)
                        .tag(ACCOUNT_TAG_ZERO_RESPONSE)
                        .backgroundColor(Color.WHITE)
                        .build());
            }

                mAdapter.add(new MaterialSimpleListItem.Builder(this)
                        .content(R.string.account_create_account)
                        .icon(R.drawable.ic_account_icon)
                        .tag(CREATE_ACCOUNT_TAG)
                        .iconPaddingDp(8)
                        .build());
        }

    }

    private void openProfilePageWithExistingAccount(String item) {
        getAccountByType(item);
    }

    private void onChooseAccountListItemSelected(MaterialSimpleListItem item) {
        mMaterialDialog.dismiss();
        if (item.getTag() != null && item.getTag().toString().equals(CREATE_ACCOUNT_TAG)) {
            openCrateAccountDialog(item);
        } else if (item.getTag() != null && item.getTag().toString().equals(ACCOUNT_TAG_ZERO_RESPONSE)) {
            createDefaultAccount(item);
        } else {
            getAccountByType(item.getContent().toString());
        }

    }

    private void getAccountByType(String item) {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_ACCOUNT_SELECTION_MADE,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, item);

        presenter.getAccountByType(item);
    }

    private void createDefaultAccount(MaterialSimpleListItem item) {
        createAccount(item.getContent().toString());
    }

    private void createAccount(String accountName) {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_CREATE_NEW_ACCOUNT_CLICKED,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_EVENT_SEND_REQUEST, accountName);

        JsonObject info = new JsonObject();
        CreateAccount createAccount = new CreateAccount(accountName, info);
        presenter.postCreateAccount(createAccount);
    }

    private void openCrateAccountDialog(MaterialSimpleListItem item) {
        new MaterialDialog.Builder(this)
                .title(R.string.account_create_account)
                .content(R.string.account_select_account_name)
                .inputType(InputType.TYPE_CLASS_TEXT)
                .input(R.string.account_dialog_account_name_hint, R.string.account_dialog_account_name_hint, new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        createAccount(input.toString());
                    }
                }).show();
    }

    private void chooseAccountClickedListener() {
        mLambdaAnalyticsEventManager.sendEvent(LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_CATEGORY_CHOOSE_ACCOUNT_ACTIVITY,
                LambdaAnalyticsEventManager.LAMBDA_EVENT_MANAGER_ACTION_SELECT_ACCOUNT_BUTTON_CLICKED,
                "","");

        mMaterialDialog = new MaterialDialog.Builder(this)
                .title(R.string.account_select_account)
                .adapter(mAdapter, null)
                .show();
    }


    private void openProfilePage() {
       // Intent i = new Intent(this, UserProfileMainPagePage.class);
        Intent i = new Intent(this, AccessDevicesCarouselActivity.class);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        showDialogMassage();
    }

    private void showDialogMassage() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    doLogUot();
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.dialog_massage_massage)).setPositiveButton(getResources().getString(R.string.dialog_massage_yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.dialog_massage_no), dialogClickListener).show();

    }

    private void doLogUot() {
        RefreshToken refreshToken = new RefreshToken(authenticationProvider.getRefreshToken());
        Observable observable = authInteractor.putLogout(refreshToken);
        observable.subscribeWith(new DisposableObserver<LogoutResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onNext(LogoutResponse logoutResponse) {
                progressBar.setVisibility(View.GONE);
                logoutSucceeded();
            }

            @Override
            public void onError(Throwable e) {
                progressBar.setVisibility(View.GONE);
                if (e instanceof HttpException) {
                    HttpException httpException = (HttpException) e;
                    System.out.println(httpException.getMessage());
                }
            }
        });
        progressBar.setVisibility(View.VISIBLE);
    }

    public void logoutSucceeded() {
        deleteAccountTokenIfExist();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LambdaConstant.SHOW_SPLASH_FLAG, false);
        startActivity(intent);
    }


    private void deleteAccountTokenIfExist() {
        if (!authenticationProvider.getAccountToken().equals("")) {
            authenticationProvider.updateStorageAccountTokenAndID("", "");
        }
    }

}
