package com.flir.sdk.models.Playback;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class ClipsResponse {

    @SerializedName("startTime")
    @Expose
    public String startTime;

    @SerializedName("endTime")
    @Expose
    public String endTime;

    @SerializedName("recordingId")
    @Expose
    public String recordingId;

}

