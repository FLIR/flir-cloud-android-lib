package com.flir.sdk.models.userModel;

import com.flir.sdk.models.authenticationModel.SignUp;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 16/03/2017.
 */

public class UpdateUserResponse {
    @SerializedName("id")
    @Expose
    public String id;
    @SerializedName("userInfo")
    @Expose
    public UserInfo userInfo;
}
