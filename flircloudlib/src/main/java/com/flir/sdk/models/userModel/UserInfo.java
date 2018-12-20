package com.flir.sdk.models.userModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 16/03/2017.
 */

public class UserInfo {
    @SerializedName("firstName")
    @Expose
    public String firstName;
    @SerializedName("lastName")
    @Expose
    public String lastName;
    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("pictureUrl")
    @Expose
    public String pictureUrl;

    @SerializedName("phone")
    @Expose
    public String phone;

}
