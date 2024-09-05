package org.apache.coyote.http11.request;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestLine {

    private static final Logger log = LoggerFactory.getLogger(RequestLine.class);

    private final String method;
    private final String path;
    private final String queryString;
    private final String version;

    public RequestLine(String method, String path, String queryString, String version) {
        this.method = method;
        this.path = path;
        this.queryString = queryString;
        this.version = version;

        log.info("request Line: {} {} {} {}", method, path, queryString, version);
    }

    public boolean hasQueryString() {
        return queryString != null;
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getQueryString() {
        return queryString;
    }

    public String getVersion() {
        return version;
    }
}
