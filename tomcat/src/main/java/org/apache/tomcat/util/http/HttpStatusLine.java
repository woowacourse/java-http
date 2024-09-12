package org.apache.tomcat.util.http;

import static org.apache.catalina.connector.HttpResponse.NEW_LINE;

public class HttpStatusLine {
    private final HttpVersion version;
    private HttpStatus status;

    public HttpStatusLine(HttpVersion version) {
        this.version = version;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public String buildResponse() {
        return version.getVersion() +
                " " +
                status.getCode() +
                " " +
                status.name() +
                NEW_LINE;
    }
}
