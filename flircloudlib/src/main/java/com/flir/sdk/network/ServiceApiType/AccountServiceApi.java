package com.flir.sdk.network.ServiceApiType;

import com.flir.sdk.models.accounts.Account;
import com.flir.sdk.models.accounts.CreateAccount;
import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.accounts.GetAccountResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Moti on 15-May-17.
 */

public interface AccountServiceApi {

    //Account service API
    @POST("/api/account/accounts")
    Observable<CreateAccountResponse> createAccount(@Header("Content-Type") String content_type, @Body CreateAccount createAccount);

    @GET("/api/account/accounts/self")
    Observable<GetAccountResponse> getAccountByType(@Header("Content-Type") String content_type, @Query("accountType") String accountType);

    @GET("/api/account/accounts")
    Observable<List<CreateAccountResponse>> getAllAccounts(@Header("Content-Type") String content_type);

    @PUT("/api/account/accounts")
    Observable<CreateAccountResponse> updateAccount(@Header("Content-Type") String content_type, @Body CreateAccount createAccount);
}
