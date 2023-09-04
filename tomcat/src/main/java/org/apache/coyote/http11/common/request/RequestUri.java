package org.apache.coyote.http11.common.request;

public class RequestUri {
    private final String detail;

    private RequestUri(final String detail) {
        this.detail = detail;
    }

    public static RequestUri create(String line) {
        int idx = line.indexOf("?");
        if (idx == -1) {
            return new RequestUri(line);
        }

        return new RequestUri(line.substring(0, idx));
    }

    public String getExtension() {
        int idx = detail.indexOf(".");
        return detail.substring(idx);
    }

    public String getDetail() {
        return detail;
    }
}
