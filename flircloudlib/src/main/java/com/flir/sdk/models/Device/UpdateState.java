package com.flir.sdk.models.Device;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class UpdateState {

    @SerializedName("key")
    @Expose
    public String key;

    @SerializedName("value")
    @Expose
    public String value;

    @SerializedName("version")
    @Expose
    public int version;

    @SerializedName("channel")
    @Expose
    public String channel;


    public UpdateState(String key, String value, int version, String expiredAt) {
        this.key = key;
        this.value = value;
        this.version = version;
    }

    public UpdateState(String key, String value, int version) {
        this.key = key;
        this.value = value;
        this.version = version;
    }

    public UpdateState(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public UpdateState() {

    }


    public void setKey(String key) {
        this.key = key;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}

