package com.flir.sdk.models.userModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenExchangeResponse {

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
