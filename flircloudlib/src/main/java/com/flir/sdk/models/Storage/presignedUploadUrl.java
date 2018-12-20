package com.flir.sdk.models.Storage;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class presignedUploadUrl {

    @SerializedName("fileId")
    @Expose
    public String fileId;

    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("headers")
    @Expose
    public JsonObject headers;


}

