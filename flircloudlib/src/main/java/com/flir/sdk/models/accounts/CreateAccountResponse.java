package com.flir.sdk.models.accounts;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 19/04/2017.
 */

public class CreateAccountResponse {

    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("owner")
    @Expose
    public String owner;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("plan")
    @Expose
    public String plan;

    @SerializedName("accountType")
    @Expose
    public String accountType;

    @SerializedName("dateAdded")
    @Expose
    public String dateAdded;

    @SerializedName("roles")
    @Expose
    public String [] roles;

    @SerializedName("info")
    @Expose
    public JsonObject info;

}
