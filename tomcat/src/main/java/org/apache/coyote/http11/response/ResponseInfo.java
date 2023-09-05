package org.apache.coyote.http11.response;

import java.net.URL;

public class ResponseInfo {
    private final URL resource;
    private final int httpStatus;
    private final String statusName;
    private final String cookie;

    public ResponseInfo(URL resource, int httpStatus, String statusName) {
        this(resource, httpStatus, statusName, null);
    }

    public ResponseInfo(URL resource, int httpStatus, String statusName, String cookie) {
        this.resource = resource;
        this.httpStatus = httpStatus;
        this.statusName = statusName;
        this.cookie = cookie;
    }

    public URL getResource() {
        return resource;
    }

    public int getHttpStatus() {
        return httpStatus;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getCookie() {
        return cookie;
    }
}
