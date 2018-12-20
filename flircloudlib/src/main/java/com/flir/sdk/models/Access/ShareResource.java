package com.flir.sdk.models.Access;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class ShareResource {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("email")
    @Expose
    public String email;

    public ShareResource(String serial, String email) {
        this.id = serial;
        this.email = email;
    }
}

