package com.flir.sdk.models.authenticationModel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti Amar on 07/03/2017.
 */

public class SignUp {

    @SerializedName("firstName")
    @Expose
    public String firstName;

    @SerializedName("lastName")
    @Expose
    public String lastName;

    @SerializedName("email")
    @Expose
    public String email;

    @SerializedName("password")
    @Expose
    public String password;

    @SerializedName("redirectUrl")
    @Expose
    public String redirectUrl;

    //In format YYYY-MM-DD i.e. 2016-11-26
    @SerializedName("birthday")
    @Expose
    public String birthday;

    @SerializedName("title")
    @Expose
    public String title;

    @SerializedName("gender")
    @Expose
    public String gender;


    @SerializedName("phone")
    @Expose
    public String phone;

    @SerializedName("location")
    @Expose
    public SignUpAddress address;


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setTitle(String title) {
        this.title = title;
    }


}
