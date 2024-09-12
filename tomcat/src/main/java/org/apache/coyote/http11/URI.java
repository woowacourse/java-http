package org.apache.coyote.http11;

import java.util.Map;

public class URI {
    private final String path;
    private final Map<String, String> parameters;

    public URI(String path, Map<String, String> parameters) {
        this.path = path;
        this.parameters = parameters;
    }
}
