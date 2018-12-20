package com.flir.sdk.models.accounts;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 19/04/2017.
 */

public class CreateAccount {

    @SerializedName("accountType")
    @Expose
    public String accountType;

    @SerializedName("info")
    @Expose
    public JsonObject info;

    public CreateAccount(String accountType, JsonObject info) {
        this.accountType = accountType;
        this.info = info;
    }
}
