package com.flir.sdk.models.Device;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class DeviceDetails {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("serial")
    @Expose
    public String serial;

    @SerializedName("account")
    @Expose
    public String account;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("tags")
    @Expose
    public JsonObject tags;

    @SerializedName("isAttached")
    @Expose
    public boolean isAttached;

    @SerializedName("isShared")
    @Expose
    public boolean isShared;


}

