package com.flir.cloud.SharedPreferences;

/**
 * Created by Moti on 13-Jun-17.
 */

public enum SharedPrefValueEnum {

    //State
    STATE_REPORTED_VALUE_KEY("reportedValue"),
    STATE_DESIRED_VALUE_KEY("desiredValue"),
    STATE_CHANNEL_KEY("channel"),
    STATE_VERSION_KEY("version"),
    STATE_UPDATE_TIME_KEY("updatedTime");

    private final String code;

    SharedPrefValueEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
