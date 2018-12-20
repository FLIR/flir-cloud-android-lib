package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 12/03/2017.
 */

public class Login {
    @SerializedName("username")
    @Expose
    public String username;
    @SerializedName("password")
    @Expose
    public String password;
    @SerializedName("expirationDuration")
    @Expose
    public long expirationDuration;

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setExpirationDuration(int expirationDuration) {
        this.expirationDuration = expirationDuration;
    }
}
