package org.apache.coyote.http11.Response;

public class HttpResponse {

    private static final String HTTP_VERSION = "HTTP/1.1";

    private StatusLine statusLine;
    private final ResponseHeader header = new ResponseHeader();
    private String responseBody;

    public HttpResponse() {
    }

    public static HttpResponse ok(final String body) {
        final HttpResponse response = new HttpResponse();
        response.statusLine = new StatusLine(HTTP_VERSION, 200, "OK");
        response.responseBody = body;
        response.header.addHeader("Content-Length", String.valueOf(body.getBytes().length));
//        response.header.addContentType("charset=utf-8");

        return response;
    }

    public static HttpResponse found(final String location) {
        final HttpResponse response = new HttpResponse();
        response.statusLine = new StatusLine(HTTP_VERSION, 302, "Found");
        response.header.setLocation(location);

        return response;
    }

    public static HttpResponse notFound() {
        final HttpResponse response = new HttpResponse();
        response.statusLine = new StatusLine(HTTP_VERSION, 404, "Not Found");
        response.header.setLocation("/404.html");

        return response;
    }

    public static HttpResponse methodNotAllowed() {
        final HttpResponse response = new HttpResponse();
        response.statusLine = new StatusLine(HTTP_VERSION, 405, "Method Not Allowed");

        return response;
    }

    public String toResponse() {
        return statusLine.getResponse() + "\r\n" +
                header.getResponse() + "\r\n" +
                "\r\n" +
                responseBody;
    }

    public HttpResponse cookie(final String cookie) {
        if (cookie == null) {
            return this;
        }
        header.setCookie(cookie);
        return this;
    }

    public HttpResponse contentType(final String contentType) {
        if (contentType == null) {
            return this;
        }
        header.addContentType(contentType);
        return this;
    }
}
