package org.apache.catalina.response;

import org.apache.catalina.http.ContentType;

public class HttpResponse {
    private static final String DEFAULT_CHARSET = "charset=utf-8";

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private final String body;

    public HttpResponse(StatusLine statusLine, ContentType contentType, String body) {
        this.statusLine = statusLine;
        this.responseHeader = new ResponseHeader();
        this.body = body;

        responseHeader.setContentType(contentType.getValue() + ";" + DEFAULT_CHARSET);
        responseHeader.setContentLength(String.valueOf(body.getBytes().length));
    }

    public void setCookie(String value) {
        responseHeader.setCookie(value);
    }

    public void addLocation(String url) {
        responseHeader.setLocation("http://localhost:8080" + url);
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
