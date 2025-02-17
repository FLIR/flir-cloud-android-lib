package com.flir.sdk.models.Storage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class VolumeDetails {

    @SerializedName("name")
    @Expose
    public String VolumeName;

    public VolumeDetails(String volumeName) {
        VolumeName = volumeName;
    }
}

