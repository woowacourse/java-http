package org.apache.coyote.http11.request;

import java.util.List;

public class Uri {

    private final HttpMethod httpMethod;
    private final String path;
    private final String httpVersion;

    private Uri(final HttpMethod httpMethod, final String path, final String httpVersion) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.httpVersion = httpVersion;
    }

    public static Uri from(final List<String> requestLines) {
        return new Uri(
                HttpMethod.from(requestLines.get(0).split(" ")[0]),
                requestLines.get(0).split(" ")[1],
                requestLines.get(0).split(" ")[2]
        );
    }

    public boolean isSamePath(final String path) {
        if (hasQueryParams()) {
            return this.path.substring(0, this.path.indexOf("?")).equals(path);
        }

        return this.path.equals(path);
    }

    public boolean hasQueryParams() {
        return path.contains("?");
    }

    public String getQueryParams() {
        int queryIndex = path.indexOf("?");
        return path.substring(queryIndex + 1);
    }

    public boolean hasResource() {
        return path.contains(".");
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
