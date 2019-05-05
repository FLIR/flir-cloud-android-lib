package com.flir.sdk.models.Device;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class GetPendingDevicesResponse {
    @SerializedName("serial")
    @Expose
    public String serial;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("tags")
    @Expose
    public JsonObject tags;

    @SerializedName("expiredAt")
    @Expose
    public String expirationTime;

    public GetPendingDevicesResponse(String serial, String name, JsonObject tags, String expirationTime) {
        this.serial = serial;
        this.name = name;
        this.tags = tags;
        this.expirationTime = expirationTime;
    }
}

