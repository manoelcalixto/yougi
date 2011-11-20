package org.cejug.entity;

/**
 * Indicates the type of deactivation that the member was subject to. In case of
 * ADMINISTRATIVE, the member was deactivated by a JUG leader. If OWNWILL, the
 * member requested his/her own deactivation.
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