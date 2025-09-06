package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class StartLine {
    private static final String RESOURCE_PATH = "static";
    private static final String ROUTE_PATH = "/";

    private final HttpMethod httpMethod;
    private final String uri;
    private final HttpVersion httpVersion;

    public StartLine(final HttpMethod httpMethod, final String uri, final HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
    }

    public boolean isStatic() {
        if (uri.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$")) {
            return true;
        }

        return httpMethod == HttpMethod.GET && !uri.contains("?");
    }

    public String extractStaticPath() {
        if (uri.equals(ROUTE_PATH)) {
            return uri;
        }

        String path = uri;
        if (!path.matches(".*\\.(html|css|js|png|jpg|jpeg|gif|ico)$")) {
            path += ".html";
        }

        return RESOURCE_PATH + path;
    }

    public Map<String, String> extractQuerystring() {
        int index = uri.indexOf("?");
        String query = uri.substring(index + 1);

        Map<String, String> params = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }

        return params;
    }

    public String getUri() {
        return uri;
    }
}
