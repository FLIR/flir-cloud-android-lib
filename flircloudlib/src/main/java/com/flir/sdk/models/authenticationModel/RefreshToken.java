package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 15/03/2017.
 */

public class RefreshToken {
    @SerializedName("refreshToken")
    @Expose
    public String refreshToken;

    public RefreshToken(String token) {
        this.refreshToken = token;
    }
}
