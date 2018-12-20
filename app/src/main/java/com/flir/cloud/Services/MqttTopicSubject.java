package com.flir.cloud.Services;

/**
 * Created by Moti on 19-Jun-17.
 */

public enum MqttTopicSubject {

    MQTT_TOPIC_SUBJECT_DEVICE("Device"),
    MQTT_TOPIC_SUBJECT_STATE("State");

    private final String code;

    MqttTopicSubject(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }


}
