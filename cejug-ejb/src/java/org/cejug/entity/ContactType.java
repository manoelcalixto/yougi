package org.cejug.entity;

/**
 * Defines different types of user contacts.
 * @author Hildeberto Mendonca
 */
public enum ContactType {

    MAIN("main"), WORK("work"), SCHOOL("school");

    private String name;

    ContactType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}