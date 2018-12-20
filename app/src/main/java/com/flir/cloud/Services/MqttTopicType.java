package com.flir.cloud.Services;

/**
 * Created by Moti on 19-Jun-17.
 */

public enum MqttTopicType {

    MQTT_TOPIC_TYPE_LIVE("Live"),
    MQTT_TOPIC_TYPE_UPDATE("Update"),
    MQTT_TOPIC_TYPE_FILE("File");

    private final String code;

    MqttTopicType(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}
