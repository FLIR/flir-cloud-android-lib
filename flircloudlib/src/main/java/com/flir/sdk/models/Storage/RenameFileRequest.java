package com.flir.sdk.models.Storage;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class RenameFileRequest {

    @SerializedName("fileName")
    @Expose
    public String newFileName;

    public RenameFileRequest(String newFileName) {
        this.newFileName = newFileName;
    }
}

