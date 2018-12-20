package com.flir.sdk.models.userModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 16/03/2017.
 */

public class ForgetPassword {

    @SerializedName("email")
    @Expose
    public String email;

    public ForgetPassword(String email) {
        this.email = email;
    }
}
