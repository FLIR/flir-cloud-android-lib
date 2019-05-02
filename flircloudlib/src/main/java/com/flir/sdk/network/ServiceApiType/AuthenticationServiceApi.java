package com.flir.sdk.network.ServiceApiType;

import com.flir.sdk.models.accounts.Account;
import com.flir.sdk.models.accounts.CreateAccount;
import com.flir.sdk.models.accounts.CreateAccountResponse;
import com.flir.sdk.models.authenticationModel.ResendVerificationRequest;
import com.flir.sdk.models.userModel.ChangePassword;
import com.flir.sdk.models.userModel.ForgetPassword;
import com.flir.sdk.models.authenticationModel.Login;
import com.flir.sdk.models.authenticationModel.LoginResponse;
import com.flir.sdk.models.authenticationModel.LogoutResponse;
import com.flir.sdk.models.authenticationModel.RefreshToken;
import com.flir.sdk.models.authenticationModel.RefreshTokenResponse;
import com.flir.sdk.models.authenticationModel.SignUp;
import com.flir.sdk.models.authenticationModel.SignUpResponse;
import com.flir.sdk.models.userModel.PasswordPolicy;
import com.flir.sdk.models.userModel.TokenExchange;
import com.flir.sdk.models.userModel.TokenExchangeResponse;
import com.flir.sdk.models.userModel.UpdateUserResponse;
import com.flir.sdk.models.userModel.UserPicture;

import io.reactivex.Completable;
import io.reactivex.Observable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

/**
 * Created by Moti Amar on 14/03/2017.
 */

public interface AuthenticationServiceApi {
    //Registration API
    @POST("/api/authentication/signup")
    Observable<SignUpResponse> signUpToServer(@Header("Content-Type") String content_type, @Body SignUp signUp);

    @POST("/api/authentication/resend")
    Completable ResendVerification(@Header("Content-Type") String content_type, @Body ResendVerificationRequest signUp);

    //Authentication API
    @POST("/api/authentication/login")
    Observable<LoginResponse> loginToServer(@Header("Content-Type") String content_type, @Body Login login);

    @PUT("/api/authentication/refresh")
    Observable<RefreshTokenResponse> refreshToken(@Header("Content-Type") String content_type, @Body RefreshToken refreshToken);

    @PUT("/api/authentication/refresh")
    Call<RefreshTokenResponse> refreshTokenSync(@Header("Content-Type") String content_type, @Body RefreshToken refreshToken);

    @PUT("/api/authentication/logout")
    Call<LogoutResponse> logoutUserSync(@Header("Content-Type") String content_type,
                                          @Body RefreshToken refreshToken);
    @PUT("/api/authentication/logout")
    Observable<LogoutResponse> logoutUser(@Header("Content-Type") String content_type,
                                          @Body RefreshToken refreshToken);
    @GET("/api/authentication/verify")
    Observable<LogoutResponse> verifySession(@Header("Content-Type") String content_type);

    //User API
    @POST("/api/authentication/forgot-password")
    Completable forgotPassword(@Header("Content-Type") String content_type,
                               @Body ForgetPassword forgetPassword);

    @PUT("/api/authentication/user")
    Observable<UpdateUserResponse> updateUser(@Header("Content-Type") String content_type,
                                              @Body SignUp signUp);

    @GET("/api/authentication/password-policy")
    Observable<PasswordPolicy> GetPasswordPolicy();

    @PUT("/api/authentication/user/password")
    Completable changePassword(@Header("Content-Type") String content_type,
                               @Body ChangePassword changePassword);

    @GET("/api/authentication/user")
    Observable<UpdateUserResponse> getSelfUser(@Header("Content-Type") String content_type);

    @POST("/api/authentication/user/picture")
    Completable uploadUserProfilePicture(@Header("Content-Type") String content_type, @Body RequestBody photo);

    @GET("/api/authentication/user/picture")
    Observable<UserPicture> getUserPicture(@Header("Content-Type") String content_type);

    @POST("/api/authentication/oauth2/token")
    Observable<TokenExchangeResponse> TokenExchange(@Header("Content-Type") String content_type, TokenExchange aTokenExchange);


}
