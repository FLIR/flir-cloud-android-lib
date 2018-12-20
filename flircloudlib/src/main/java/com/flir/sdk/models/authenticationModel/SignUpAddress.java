package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 07/03/2017.
 */

public class SignUpAddress {

    @SerializedName("country")
    @Expose
    public String country;

    @SerializedName("city")
    @Expose
    public String city;

    @SerializedName("street")
    @Expose
    public String street;

    @SerializedName("postalCode")
    @Expose
    public String postalCode;
}
