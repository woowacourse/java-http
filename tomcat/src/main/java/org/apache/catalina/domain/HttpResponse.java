package org.apache.catalina.domain;

import com.http.enums.HttpStatus;
import java.util.Map;

public final class HttpResponse {

    private ResponseStartLine startLine;
    private final HttpHeader header;
    private byte[] body;

    public HttpResponse(ResponseStartLine startLine, HttpHeader headers, byte[] body) {
        this.startLine = startLine;
        this.header = headers;
        this.body = body;
    }

    public HttpResponse(String version) {
        this(new ResponseStartLine(version), new HttpHeader(), null);
    }

    public HttpResponse(HttpRequest request) {
        this(new ResponseStartLine(request), new HttpHeader(), null);
    }

    public ResponseStartLine getStartLine() {
        return startLine;
    }

    public void setStartLine(ResponseStartLine startLine) {
        this.startLine = startLine;
    }

    public void addHeader(String key, String value) {
        this.header.put(key, value);
    }

    public Map<String, String> getHeaders() {
        return header.headers();
    }

    public HttpStatus getStatus() {
        return this.startLine.getHttpStatus();
    }

    public void setStatus(HttpStatus httpStatus) {
        startLine.setHttpStatus(httpStatus);
    }

    public void setVersion(String version) {
        startLine.setVersion(version);
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

}
