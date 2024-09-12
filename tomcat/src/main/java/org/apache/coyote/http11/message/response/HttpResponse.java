package org.apache.coyote.http11.message.response;

import org.apache.coyote.http11.message.common.HttpHeaders;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String NEWLINE = "\r\n";
    private static final String END_OF_HEADERS = "";

    private StatusLine statusLine;
    private HttpHeaders headers;
    private String body = "";

    public HttpResponse() {
        headers = new HttpHeaders();
    }

    public void setStatus(StatusCode statusCode) {
        statusLine = new StatusLine(HTTP_VERSION, statusCode);
    }

    public void addHeader(String header, String value) {
        headers.add(header, value);
    }

    public void setBody(String body) {
        headers.add("Content-Length", String.valueOf(body.getBytes().length));
        this.body = body;
    }

    public void sendRedirect(String url) {
        setStatus(StatusCode.FOUND);
        addHeader("Location", url);
        addHeader("Content-Length", "0");
    }

    public String convertMessage() {
        return String.join(
                NEWLINE,
                statusLine.convertMessage(),
                headers.convertMessage(),
                END_OF_HEADERS,
                body
        );
    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusLine=" + statusLine +
                ", headers=" + headers +
                ", bodyLength='" + body.getBytes().length + '\'' +
                '}';
    }
}
