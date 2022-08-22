package com.coldbrewcode.contact.config;

import lombok.Getter;

public class ConfigRepo {

    @Getter
    private String fromEmailAddress = getEnvVar("NO_REPLY_EMAIL_ADDRESS");

    private String getEnvVar(String key) {
        return System.getenv(key);
    }

}
