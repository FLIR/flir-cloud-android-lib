package com.flir.sdk.models.Access;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class ResourceResponse {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("resource_type")
    @Expose
    public String resourceType;

    @SerializedName("account")
    @Expose
    public String account;

    @SerializedName("accountType")
    @Expose
    public String accountType;

    @SerializedName("tags")
    @Expose
    public JsonObject tags;

    @Override
    public String toString() {
        return "{" +
                "id='" + id + '\'' + "\n" +
                "name='" + name + '\'' + "\n" +
                "account='" + account + '\'' + "\n" +
                "resourceType='" + resourceType + '\'' + "\n" +
                "accountType='" + accountType + '\'' + "\n" +
                "tags=" + tags +
                '}';
    }
}

