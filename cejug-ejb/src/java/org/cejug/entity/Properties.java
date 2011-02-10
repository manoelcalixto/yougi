package org.cejug.entity;

public enum Properties {
    FILE_REPOSITORY_PATH("fileRepositoryPath"),
    URL("url"),
    GROUP_NAME("groupName");

    private String name;

    Properties(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}