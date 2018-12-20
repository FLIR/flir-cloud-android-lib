package com.flir.sdk.models.Device;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 21-May-17.
 */

public class UploadDownloadUrlResponse {


    @SerializedName("url")
    @Expose
    public String url;

    @SerializedName("headers")
    @Expose
    public JsonObject presignedUploadUrlResponseHeadersValues;

    @Override
    public String toString() {
        return "{" +
                "url='" + url + '\'' + "\n" +
                "presignedUploadUrlResponseHeadersValues=" + presignedUploadUrlResponseHeadersValues + "\n" +
                '}';
    }
}
