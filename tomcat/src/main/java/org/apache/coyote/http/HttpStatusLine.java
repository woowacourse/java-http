package org.apache.coyote.http;

public class HttpStatusLine {

    private static final String SEPARATOR = " ";
    private static final int SPLIT_SIZE = 2;

    private static final int VERSION_INDEX = 0;
    private static final int STATUS_CODE_INDEX = 1;

    private final HttpVersion version;
    private final HttpStatusCode statusCode;

    public HttpStatusLine(final HttpVersion version, final HttpStatusCode statusCode) {
        this.version = version;
        this.statusCode = statusCode;
    }

    public HttpStatusLine(final String statusLine) {
        String[] split = statusLine.split(SEPARATOR, SPLIT_SIZE);
        this.version = HttpVersion.valueOf(split[VERSION_INDEX]);
        this.statusCode = HttpStatusCode.from(split[STATUS_CODE_INDEX]);
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String asString() {
        return version.value() + SEPARATOR + statusCode.asString();
    }
}
