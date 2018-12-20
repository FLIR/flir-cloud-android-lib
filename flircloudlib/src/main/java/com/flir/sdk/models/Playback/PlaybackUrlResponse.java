package com.flir.sdk.models.Playback;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class PlaybackUrlResponse {

    @SerializedName("url")
    @Expose
    public String url;

    public PlaybackUrlResponse(String url) {
        this.url = url;
    }
}

