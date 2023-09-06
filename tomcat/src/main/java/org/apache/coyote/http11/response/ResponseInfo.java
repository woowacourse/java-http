package org.apache.coyote.http11.response;

import java.net.URL;

public class ResponseInfo {
    private final URL resource;
    private final int httpStatusCode;
    private final String statusMessage;
    private final String cookie;

    public ResponseInfo(URL resource, int httpStatusCode, String statusMessage) {
        this(resource, httpStatusCode, statusMessage, null);
    }

    public ResponseInfo(URL resource, int httpStatusCode, String statusMessage, String cookie) {
        this.resource = resource;
        this.httpStatusCode = httpStatusCode;
        this.statusMessage = statusMessage;
        this.cookie = cookie;
    }

    public URL getResource() {
        return resource;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getCookie() {
        return cookie;
    }
}
