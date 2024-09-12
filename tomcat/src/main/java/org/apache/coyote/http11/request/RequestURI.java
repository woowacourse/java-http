package org.apache.coyote.http11.request;

import java.net.URI;
import java.util.Map;

public class RequestURI {

    private final URI uri;
    private final String path;
    private final String extension;
    private final Map<String, String> queryString;

    public RequestURI(URI uri, String path, String extension, Map<String, String> queryString) {
        this.uri = uri;
        this.path = path;
        this.extension = extension;
        this.queryString = queryString;
    }

    public String getPathWithExtension() {
        if (extension.isEmpty()) {
            return path;
        }
        return "%s.%s".formatted(path, extension);
    }

    public String getUri() {
        return uri.toString();
    }

    public String getExtension() {
        return extension;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQueryString() {
        return queryString;
    }
}
