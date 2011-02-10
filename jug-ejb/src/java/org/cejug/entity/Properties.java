package org.cejug.entity;

public enum Properties {
    FILE_REPOSITORY_PATH("fileRepositoryPath", ""),
    URL("url", ""),
    GROUP_NAME("groupName", ""),
    SEND_EMAILS("sendEmails", "false");

    private String name;
    private String defaultValue;

    Properties(String name, String defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public String getDefaultValue() {
        return defaultValue;
    }
}