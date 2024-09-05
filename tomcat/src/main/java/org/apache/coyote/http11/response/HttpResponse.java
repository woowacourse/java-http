package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpStatus status;
    private final String protocolVersion;
    private final ResponseHeader header;
    private ResponseBody body;

    public HttpResponse(final HttpStatus status) {
        this.status = status;
        this.protocolVersion = "HTTP/1.1";
        this.header = new ResponseHeader();
    }

    public void setRedirect(final String location) {
        header.add("Location", location);
        header.add("Content-Length", "0");
        header.add("Connection", "close");
    }

    public void setCookie(final String name, final String value) {
        header.add("Set-Cookie", name + "=" + value);
    }

    public void setBody(final ResponseBody body) {
        this.body = body;
        header.add("Content-Type", body.getContentType() + ";charset=utf-8");
        header.add("Content-Length", body.getContentLength());
    }

    public String getResponse() {
        StringBuilder responseBuilder = new StringBuilder();
        System.out.println("makeStatusLine() = " + makeStatusLine());
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
