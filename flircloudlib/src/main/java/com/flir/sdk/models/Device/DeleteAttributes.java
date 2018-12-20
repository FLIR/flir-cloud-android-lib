package com.flir.sdk.models.Device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class DeleteAttributes {

    @SerializedName("key")
    @Expose
    public String key;

    @SerializedName("channel")
    @Expose
    public String channel;

}

