package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 07/03/2017.
 */

public class ResendVerificationRequest {

    @SerializedName("email")
    @Expose
    public String email;

}