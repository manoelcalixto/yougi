package org.cejug.entity;

public enum Properties {
    FILE_REPOSITORY_PATH("fileRepositoryPath", ""),
    URL("url", ""),
    GROUP_NAME("groupName", ""),
    SEND_EMAILS("sendEmails", "false");

    private String key;
    private String defaultValue;

    Properties(String key, String defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}