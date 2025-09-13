package org.apache.catalina.controller;

import java.util.HashMap;
import java.util.Map;

public class RequestMapping {

    private final Map<String, Controller> mappings = new HashMap<>();

    public void registerMapping(final String path, final Controller controller) {
        mappings.put(path, controller);
    }

    public Controller getController(final String path) {
        return mappings.get(path);
    }

    public boolean contains(final String path) {
        return mappings.containsKey(path);
    }
}
