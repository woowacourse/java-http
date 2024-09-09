package org.apache.coyote.http11;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SessionStorage {

    private final List<UUID> storage = new ArrayList<>();

    public static UUID generateUUID() {
        return UUID.randomUUID();
    }

    public void add(final UUID uuid) {
        storage.add(uuid);
    }
}
