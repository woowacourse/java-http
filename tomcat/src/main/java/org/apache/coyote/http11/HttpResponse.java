package org.apache.coyote.http11;

import org.apache.catalina.Cookie;

public record HttpResponse(
        double httpVersion,
        int statusCode,
        String status,
        String contentType,
        int contentLength,
        String location,
        String charSet,
        String responseBody,
        Cookie cookie
) {
    private static final String DEFAULT_CHARSET = "charset=utf-8";

    public HttpResponse(
            double httpVersion,
            int statusCode,
            String status,
            String responseBody,
            String uri
    ) {
        this(
                httpVersion,
                statusCode,
                status,
                StaticResourceExtension.findMimeTypeByUrl(uri),
                responseBody != null ? responseBody.getBytes().length : 0,
                null,
                DEFAULT_CHARSET,
                responseBody,
                null
        );
    }

    public HttpResponse(
            double httpVersion,
            int statusCode,
            String status,
            String responseBody,
            String uri,
            Cookie cookie
    ) {
        this(
                httpVersion,
                statusCode,
                status,
                StaticResourceExtension.findMimeTypeByUrl(uri),
                responseBody != null ? responseBody.getBytes().length : 0,
                null,
                DEFAULT_CHARSET,
                responseBody,
                cookie
        );
    }

    public HttpResponse(
            double httpVersion,
            int statusCode,
            String status,
            String location
    ) {
        this(
                httpVersion,
                statusCode,
                status,
                null,
                0,
                location,
                null,
                null,
                null
        );
    }

    public HttpResponse(
            double httpVersion,
            int statusCode,
            String status,
            String location,
            Cookie cookie
    ) {
        this(
                httpVersion,
                statusCode,
                status,
                null,
                0,
                location,
                null,
                null,
                cookie
        );
    }

    public static HttpResponse createRedirectionResponse(HttpRequest httpRequest, String redirectionLocation) {
        return new HttpResponse(httpRequest.getHttpVersion(), HTTPStatus.FOUND.getStatusCode(),
                HTTPStatus.FOUND.getStatus(), redirectionLocation);
    }

    public static HttpResponse createOKResponse(HttpRequest httpRequest, String responseBody, String uri) {
        return new HttpResponse(httpRequest.getHttpVersion(), HTTPStatus.OK.getStatusCode(), HTTPStatus.OK.getStatus(),
                responseBody, uri);
    }


    public static HttpResponse createRedirectionResponseWithCookie(HttpRequest httpRequest,
                                                                   String redirectionLocation,
                                                                   Cookie cookie) {
        return new HttpResponse(httpRequest.getHttpVersion(), HTTPStatus.FOUND.getStatusCode(),
                HTTPStatus.FOUND.getStatus(), redirectionLocation, cookie);
    }

    public static HttpResponse createOKResponseWithCookie(HttpRequest httpRequest, String responseBody, String uri,
                                                          Cookie cookie) {
        return new HttpResponse(httpRequest.getHttpVersion(), HTTPStatus.OK.getStatusCode(), HTTPStatus.OK.getStatus(),
                responseBody, uri, cookie);
    }
}
