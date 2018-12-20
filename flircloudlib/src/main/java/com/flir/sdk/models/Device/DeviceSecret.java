package com.flir.sdk.models.Device;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class DeviceSecret {
    @SerializedName("secret")
    @Expose
    public String secret;


    public DeviceSecret(String secret) {
        this.secret = secret;
    }

}

