package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpStatus status;
    private final String protocolVersion;
    private final ResponseHeader header;
    private ResponseBody body;

    public HttpResponse(HttpStatus status, String protocolVersion, ResponseHeader header, ResponseBody body) {
        this.status = status;
        this.protocolVersion = protocolVersion;
        this.header = header;
        this.body = body;
    }

    public HttpResponse(final HttpStatus status) {
        this(status, "HTTP/1.1", new ResponseHeader(), null);
    }

    public HttpResponse(final HttpStatus status, final ResponseBody body) {
        this(status, "HTTP/1.1", new ResponseHeader(), body);
        header.add("Content-Type", body.getContentType() + ";charset=utf-8");
        header.add("Content-Length", body.getContentLength());
    }

    public void setRedirect(final String location) {
        header.add("Location", location);
        header.add("Content-Length", "0");
        header.add("Connection", "close");
    }

    public void setCookie(final String name, final String value) {
        header.add("Set-Cookie", name + "=" + value);
    }

    public String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(makeStatusLine()).append("\r\n");
        responseBuilder.append(header.getHeaderResponse()).append("\r\n");
        responseBuilder.append("\r\n");

        if (body != null) {
            responseBuilder.append(body.getValue());
        }
        return responseBuilder.toString();
    }

    private String makeStatusLine() {
        return String.format("%s %s %s ", protocolVersion, status.getCode(), status.getReasonPhrase());
    }
}
