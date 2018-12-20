package com.flir.sdk.models.Storage;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class VolumeResponse {

    @SerializedName("id")
    @Expose
    public String VolumeId;

    @SerializedName("name")
    @Expose
    public String VolumeName;

    @SerializedName("tags")
    @Expose
    public JsonObject tags;

    public VolumeResponse(String volumeId, String volumeName) {
        VolumeId = volumeId;
        VolumeName = volumeName;
    }
}

