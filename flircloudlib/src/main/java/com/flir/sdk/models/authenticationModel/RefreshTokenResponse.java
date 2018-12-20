package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 15/03/2017.
 */

public class RefreshTokenResponse {

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

    @SerializedName("typeAs")
    @Expose
    public String typeAs;

    @SerializedName("expiration")
    @Expose
    public int expiration;

}
