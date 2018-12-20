package com.flir.cloud.SharedPreferences;

/**
 * Created by Moti on 13-Jun-17.
 */

public enum SharedPrefGroupKeyEnum {

    //services names
    SD_CARD_DEVICE_RECORDING_MODE("sdCardRecording"),
    DEVICE_RESOLUTION_MODE("Resolution");

    private final String code;

    SharedPrefGroupKeyEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

}
