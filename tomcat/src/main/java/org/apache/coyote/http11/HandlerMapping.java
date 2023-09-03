package org.apache.coyote.http11;

import java.util.Arrays;
import org.apache.coyote.http11.httpmessage.request.HttpMethod;

public enum HandlerMapping {

    MAIN("/", HttpMethod.GET, "Hello world!"),
    INDEX("/index.html", HttpMethod.GET, "index.html");

    private final String path;
    private final HttpMethod httpMethod;
    private final String response;

    HandlerMapping(final String path, final HttpMethod httpMethod, final String response) {
        this.path = path;
        this.httpMethod = httpMethod;
        this.response = response;
    }

    public static HandlerMapping find(final String path, final HttpMethod httpMethod) {
        return Arrays.stream(HandlerMapping.values())
            .filter(mapping -> mapping.path.equals(path) && mapping.httpMethod == httpMethod)
            .findAny()
            .orElse(null);
    }

    public String getResponse() {
        return response;
    }
}
