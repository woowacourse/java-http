package org.apache.coyote.http11;

public record HttpResponse(
        double httpVersion,
        int statusCode,
        String status,
        String contentType,
        int contentLength,
        String location,
        String charSet,
        String responseBody
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
                responseBody
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
                null
        );
    }

    public static HttpResponse createRedirectionResponse(HttpRequest httpRequest, String redirectionLocation) {
        return new HttpResponse(httpRequest.httpVersion(), 302, "Found", redirectionLocation);
    }

    public static HttpResponse createOKResponse(HttpRequest httpRequest, String responseBody, String uri) {
        return new HttpResponse(httpRequest.httpVersion(), 200, "OK", responseBody, uri);
    }
}
