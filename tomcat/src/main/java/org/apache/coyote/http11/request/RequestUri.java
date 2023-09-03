package org.apache.coyote.http11.request;

public class RequestUri {
    private final String detail;

    private RequestUri(final String detail) {
        this.detail = detail;
    }

    public static RequestUri create(String uri) {
        return new RequestUri(uri);
    }

    public String getExtension() {
        int idx = detail.indexOf(".");
        return detail.substring(idx);
    }

    public String getDetail() {
        return detail;
    }
}
