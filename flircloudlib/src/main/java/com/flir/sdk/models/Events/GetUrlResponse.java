package com.flir.sdk.models.Events;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class GetUrlResponse {

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("expiration")
    @Expose
    public int expiration;

}

