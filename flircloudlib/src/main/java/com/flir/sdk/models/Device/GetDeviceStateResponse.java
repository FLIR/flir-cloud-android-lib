package com.flir.sdk.models.Device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 21-May-17.
 */

public class GetDeviceStateResponse {


    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("key")
    @Expose
    public String key;

    @SerializedName("channel")
    @Expose
    public String channel;

    @SerializedName("desiredValue")
    @Expose
    public String desiredValue;

    @SerializedName("reportedValue")
    @Expose
    public String reportedValue;

    @SerializedName("version")
    @Expose
    public int version;

    @SerializedName("updatedTime")
    @Expose
    public String updatedTime;

    @SerializedName("readOnly")
    @Expose
    public boolean readOnly;
}
