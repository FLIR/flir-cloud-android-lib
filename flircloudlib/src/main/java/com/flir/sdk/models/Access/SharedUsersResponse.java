package com.flir.sdk.models.Access;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Arrays;

/**
 * Created by Moti on 16-May-17.
 */

public class SharedUsersResponse {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("user")
    @Expose
    public String user;

    @SerializedName("acl")
    @Expose
    public String [] acl;

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' + "\n" +
                "email='" + email + '\'' + "\n" +
                "user='" + user + '\'' + "\n" +
                "acl=" + Arrays.toString(acl) + "\n" +
                '}';
    }
}

