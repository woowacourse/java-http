package org.apache.coyote.http11;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import org.apache.catalina.ResponseCookie;

public record HttpResponse(
        double httpVersion,
        int statusCode,
        String status,
        String contentType,
        int contentLength,
        String location,
        String charSet,
        String responseBody,
        ResponseCookie responseCookie
) {
    private static final String DEFAULT_CHARSET_VALUE = "charset=utf-8";
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

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
                responseBody != null ? responseBody.getBytes(DEFAULT_CHARSET).length : 0,
                null,
                DEFAULT_CHARSET_VALUE,
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
            ResponseCookie responseCookie
    ) {
        this(
                httpVersion,
                statusCode,
                status,
                StaticResourceExtension.findMimeTypeByUrl(uri),
                responseBody != null ? responseBody.getBytes().length : 0,
                null,
                DEFAULT_CHARSET_VALUE,
                responseBody,
                responseCookie
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
            ResponseCookie responseCookie
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
                responseCookie
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
                                                                   ResponseCookie responseCookie) {
        return new HttpResponse(httpRequest.getHttpVersion(), HTTPStatus.FOUND.getStatusCode(),
                HTTPStatus.FOUND.getStatus(), redirectionLocation, responseCookie);
    }

    public static HttpResponse createOKResponseWithCookie(HttpRequest httpRequest, String responseBody, String uri,
                                                          ResponseCookie responseCookie) {
        return new HttpResponse(httpRequest.getHttpVersion(), HTTPStatus.OK.getStatusCode(), HTTPStatus.OK.getStatus(),
                responseBody, uri, responseCookie);
    }
}
