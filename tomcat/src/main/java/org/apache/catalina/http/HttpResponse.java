package org.apache.catalina.http;

import java.util.StringJoiner;
import org.apache.catalina.http.body.HttpResponseBody;
import org.apache.catalina.http.header.HttpHeader;
import org.apache.catalina.http.header.HttpHeaders;
import org.apache.catalina.http.startline.HttpResponseLine;
import org.apache.catalina.http.startline.HttpStatus;
import org.apache.catalina.session.Session;

public class HttpResponse {

    private static final String DELIMITER = "\r\n";

    private final HttpResponseLine httpResponseLine;
    private final HttpHeaders httpHeaders;
    private final HttpResponseBody httpResponseBody;

    public HttpResponse(HttpResponseLine httpResponseLine, HttpHeaders httpHeaders, HttpResponseBody httpResponseBody) {
        this.httpResponseLine = httpResponseLine;
        this.httpHeaders = httpHeaders;
        this.httpResponseBody = httpResponseBody;
    }

    public byte[] getBytes() {
        return stringify().getBytes();
    }

    private String stringify() {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        joiner.add(httpResponseLine.stringify());
        joiner.add(httpHeaders.stringify(DELIMITER));
        joiner.add("");
        joiner.add(httpResponseBody.getValue());

        return joiner.toString();
    }

    public void addHeader(HttpHeader header, String value) {
        httpHeaders.add(header, value);
    }

    public void setStatus(HttpStatus httpStatus) {
        httpResponseLine.setStatus(httpStatus);
    }

    public void setBody(String body) {
        httpResponseBody.setValue(body);
        httpHeaders.add(HttpHeader.CONTENT_LENGTH, String.valueOf(body.getBytes().length));
    }

    public void addSessionToCookies(Session session) {
        httpHeaders.addToCookies(Session.KEY, session.getId());
    }

    public boolean isValid() {
        return httpResponseLine.isValid();
    }
}
