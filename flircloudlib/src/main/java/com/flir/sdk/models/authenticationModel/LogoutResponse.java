package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 15/03/2017.
 */

public class LogoutResponse {
    @SerializedName("token")
    @Expose
    public String token;
    @SerializedName("status")
    @Expose
    public String status;
}
