package com.flir.sdk.models.userModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenExchange {
    @SerializedName("token_id")
    @Expose
    public String token_id;

    @SerializedName("accessToken")
    @Expose
    public String accessToken;

}
