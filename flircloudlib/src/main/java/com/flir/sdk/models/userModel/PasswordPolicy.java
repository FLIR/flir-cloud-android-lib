package com.flir.sdk.models.userModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PasswordPolicy {

    @SerializedName("minimum_characters")
    @Expose
    public int minimum_characters;

    @SerializedName("maximum_characters")
    @Expose
    public int maximum_characters;

    @SerializedName("white_spaces")
    @Expose
    public String white_spaces;

    @SerializedName("non_alphanumeric")
    @Expose
    public int non_alphanumeric;

}
