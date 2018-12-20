package com.flir.sdk.models.userModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 16/03/2017.
 */

public class ChangePassword {

    @SerializedName("oldPassword")
    @Expose
    public String oldPassword;

    @SerializedName("newPassword")
    @Expose
    public String newPassword;

    public ChangePassword(String oldPassword, String newPassword) {
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }
}
