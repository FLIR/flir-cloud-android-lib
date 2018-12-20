package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 07/03/2017.
 */

public class SignUpLocation {
    @SerializedName("latitude")
    @Expose
    public String latitude;
    @SerializedName("longitude")
    @Expose
    public String longitude;
}
