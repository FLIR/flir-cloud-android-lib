package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class LoginResponse extends RefreshTokenResponse {

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("firstName")
    @Expose
    public String firstName;

    @SerializedName("refreshToken")
    @Expose
    public String refreshToken;
}
