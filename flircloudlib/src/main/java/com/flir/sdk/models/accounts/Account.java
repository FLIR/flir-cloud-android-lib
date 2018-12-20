package com.flir.sdk.models.accounts;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 19/04/2017.
 */

public class Account extends CreateAccountResponse{
    @SerializedName("accountToken")
    @Expose
    public String accountToken;
}
