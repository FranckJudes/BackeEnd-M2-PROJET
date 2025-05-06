package com.harmony.harmoniservices.core.domain.enums;

public enum TypeTask {
    NONE("none"),
    TASK("task"),
    USER("user"),
    SERVICE("service"),
    SCRIPT("script"),
    BUSINESS_RULE("businessRule"),
    SEND("send"),
    RECEIVE("receive"),
    MANUAL("manual");

    private final String value;

    TypeTask(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}