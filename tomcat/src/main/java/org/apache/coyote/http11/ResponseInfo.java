package org.apache.coyote.http11;

import java.net.URL;

public class ResponseInfo {
    private final URL resource;
    private final int httpStatus;
    private final String statusName;

    public ResponseInfo(URL resource, int httpStatus, String statusName) {
        this.resource = resource;
        this.httpStatus = httpStatus;
        this.statusName = statusName;
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
}
