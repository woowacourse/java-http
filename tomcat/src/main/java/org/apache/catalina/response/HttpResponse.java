package org.apache.catalina.response;

import org.apache.catalina.http.ContentType;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final String body;

    public HttpResponse(StatusLine statusLine, ContentType contentType, String body) {
        this.statusLine = statusLine;
        this.responseHeader = new ResponseHeader();
        this.body = body;

        responseHeader.setContentType(contentType.toString());
        responseHeader.setContentLength(String.valueOf(body.getBytes().length));
    }

    public void setCookie(String value) {
        responseHeader.setCookie(value);
    }

    public HttpResponse addLocation(String url) {
        responseHeader.setRedirection("http://localhost:8080" + url);
        return this;
    }

    public void addHeader(String key, String value) {
        responseHeader.add(key, value);
    }

    @Override
    public String toString() {
        return statusLine + " \r\n"
                + responseHeader + "\r\n"
                + body;
    }
}
