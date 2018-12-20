package com.flir.sdk.models.Device;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class UpdateReported extends UpdateState {

    @SerializedName("readOnly")
    @Expose
    public boolean readOnly;

    @SerializedName("possibleValues")
    @Expose
    public String []  possibleValues;

}

