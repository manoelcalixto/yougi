package org.cejug.entity;

/**
 *
 * @author Hildeberto Mendonca
 */
public enum DeactivationType {

    ADMINISTRATIVE("administrative"), OWNWILL("ownwill");

    private String name;

    DeactivationType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }
}