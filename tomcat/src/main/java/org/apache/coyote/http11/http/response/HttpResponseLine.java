package org.apache.coyote.http11.http.response;

import org.apache.coyote.http11.http.common.startline.HttpVersion;

public class HttpResponseLine {

    HttpVersion version;
    HttpStatus status;

    private HttpResponseLine(final HttpVersion version, final HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    public static HttpResponseLine of(final HttpVersion version, final HttpStatus status) {
        return new HttpResponseLine(version, status);
    }

    public String getFormat() {
        return version.getValue() + " " + status.getFormattedName() + " ";
    }
}
