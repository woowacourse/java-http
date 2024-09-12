package org.apache.catalina.response;

import org.apache.catalina.http.ContentType;
import org.apache.catalina.request.HttpRequest;

public class HttpResponse {

    private final StatusLine statusLine;
    private final ResponseHeader responseHeader;
    private String body;

    public HttpResponse(StatusLine statusLine, ContentType contentType, String body) {
        this.statusLine = statusLine;
        this.responseHeader = new ResponseHeader();
        this.body = body;

        responseHeader.setContentType(contentType.toString());
        responseHeader.setContentLength(String.valueOf(body.getBytes().length));
    }

    public static HttpResponse of(HttpRequest request) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), HttpStatus.OK),
                request.getContentType(),
                ""
        );
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        statusLine.setHttpStatus(httpStatus);
    }

    public void setContentType(ContentType contentType) {
        responseHeader.setContentType(contentType.toString());
    }

    public void setCookie(String value) {
        responseHeader.setCookie(value);
    }

    public void setBody(String body) {
        this.body = body;
        responseHeader.setContentLength(String.valueOf(body.getBytes().length));
    }

    public HttpResponse addLocation(String url) {
        responseHeader.setRedirection("http://localhost:8080" + url);
        return this;
    }

    public HttpResponse addHeader(String key, String value) {
        responseHeader.add(key, value);
        return this;
    }

    @Override
    public String toString() {
        return statusLine + " \r\n"
                + responseHeader + "\r\n"
                + body;
    }
}
