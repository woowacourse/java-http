package org.apache.catalina.response;

import org.apache.catalina.http.ContentType;
import org.apache.catalina.reader.FileReader;
import org.apache.catalina.request.HttpRequest;

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

    public static HttpResponse createRedirectResponse(HttpRequest request, HttpStatus status, String path) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), status),
                request.getContentType(),
                FileReader.loadFileContent(path)
        ).addLocation(path);
    }

    public static HttpResponse createFileOkResponse(HttpRequest request, String path) {
        return createFileResponse(request, HttpStatus.OK, path);
    }

    public static HttpResponse createFileResponse(HttpRequest request, HttpStatus status, String path) {
        return new HttpResponse(
                new StatusLine(request.getVersionOfProtocol(), status),
                request.getContentType(),
                FileReader.loadFileContent(path)
        );
    }

    public void setCookie(String value) {
        responseHeader.setCookie(value);
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
