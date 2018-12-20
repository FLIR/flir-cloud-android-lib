package com.flir.sdk.models.Device;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class UpdateDevice {

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("tags")
    @Expose
    public JsonObject tags;

    public UpdateDevice(String name, JsonObject tags) {
        this.name = name;
        this.tags = tags;
    }

    public void setTags(JsonObject tags) {
        this.tags = tags;
    }
}

