package com.flir.sdk.models.Access;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Moti on 16-May-17.
 */

public class AddResource {
    @SerializedName("id")
    @Expose
    public String id;

    @SerializedName("name")
    @Expose
    public String name;

    @SerializedName("resource_type")
    @Expose
    public String resource_type;

    @SerializedName("tags")
    @Expose
    public JsonObject tags;

    public AddResource(String serial, String resource_type, String name, JsonObject tags) {
        this.id = serial;
        this.name = name;
        this.tags = tags;
        this.resource_type = resource_type;
    }

    public void setTags(JsonObject tags) {
        this.tags = tags;
    }
}

