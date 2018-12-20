package com.flir.sdk.models.Storage;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class GetFileResponse {

    @SerializedName("fileId")
    @Expose
    public String fileId;

    @SerializedName("accountId")
    @Expose
    public String accountId;

    @SerializedName("volume")
    @Expose
    public String volume;

    @SerializedName("fileName")
    @Expose
    public String fileName;


    @SerializedName("size")
    @Expose
    public int size;


    @SerializedName("created")
    @Expose
    public String created;


    @SerializedName("modified")
    @Expose
    public String modified;

    @SerializedName("tags")
    @Expose
    public JsonObject [] tags;

}

