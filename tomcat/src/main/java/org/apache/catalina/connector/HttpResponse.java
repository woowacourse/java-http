package org.apache.catalina.connector;

import java.util.Objects;

import org.apache.tomcat.http.common.Version;
import org.apache.tomcat.http.common.body.Body;
import org.apache.tomcat.http.common.body.TextTypeBody;
import org.apache.tomcat.http.exception.NotFoundException;
import org.apache.tomcat.http.response.ResponseHeader;
import org.apache.tomcat.http.response.StatusCode;
import org.apache.tomcat.http.response.StatusLine;

public class HttpResponse {

    private StatusLine statusLine;
    private ResponseHeader responseHeader;
    private Body body;

    public HttpResponse() {
        this.statusLine = new StatusLine(new Version(1, 1), new StatusCode("NOT FOUND", 404));
        this.responseHeader = new ResponseHeader();
        this.body = new TextTypeBody("");
    }

    public HttpResponse(final StatusLine statusLine, final ResponseHeader responseHeader, final Body body) {
        this.statusLine = statusLine;
        this.responseHeader = responseHeader;
        this.body = body;
    }

    public void copyProperties(final HttpResponse httpResponse) {
        this.statusLine = httpResponse.statusLine;
        this.responseHeader = httpResponse.responseHeader;
        this.body = httpResponse.body;
    }

    public String getHeaderContent(final String key) {
        final var content = responseHeader.get(key);
        if (content.isBlank()) {
            throw new NotFoundException("헤더 값 찾을 수 없음");
        }
        return content;
    }

    public StatusLine getStatusLine() {
        return statusLine;
    }

    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public Body getBody() {
        return body;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final HttpResponse that = (HttpResponse) o;
        return Objects.equals(statusLine, that.statusLine) && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusLine, body);
    }
}
