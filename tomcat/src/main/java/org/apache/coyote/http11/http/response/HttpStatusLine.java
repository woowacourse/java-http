package org.apache.coyote.http11.http.response;

import org.apache.coyote.http11.http.common.startline.HttpVersion;

public class HttpStatusLine {

    HttpVersion version;
    HttpStatus status;

    private HttpStatusLine(final HttpVersion version, final HttpStatus status) {
        this.version = version;
        this.status = status;
    }

    public static HttpStatusLine of(final HttpVersion version, final HttpStatus status) {
        return new HttpStatusLine(version, status);
    }

    public String getFormat() {
        return version.getValue() + " " + status.getFormattedName();
    }
}
