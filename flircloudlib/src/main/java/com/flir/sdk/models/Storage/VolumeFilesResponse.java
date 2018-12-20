package com.flir.sdk.models.Storage;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class VolumeFilesResponse {
    @SerializedName("volume")
    @Expose
    public String volume;

    @SerializedName("files")
    @Expose
    public GetFileResponse [] files;

    @SerializedName("nextTicket")
    @Expose
    public String nextTicket;

}

