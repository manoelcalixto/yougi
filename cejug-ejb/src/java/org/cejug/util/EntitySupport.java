package org.cejug.util;

import java.util.UUID;

public class EntitySupport {

    public static String generateEntityId() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replaceAll("-", "").toUpperCase();
    }
}