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
    private static final double DEFAULT_HTTP_VERSION = 1.1;

    public HttpResponse(
            int statusCode,
            String status,
            String responseBody,
            String uri
    ) {
        this(
                DEFAULT_HTTP_VERSION,
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
            int statusCode,
            String status,
            String location
    ) {
        this(
                DEFAULT_HTTP_VERSION,
                statusCode,
                status,
                null,
                0,
                location,
                null,
                null
        );
    }

    public static HttpResponse createRedirectionResponse(String redirectionLocation) {
        return new HttpResponse(302, "Found", redirectionLocation);
    }

    public static HttpResponse createOKResponse(String responseBody, String uri) {
        return new HttpResponse(200, "OK", responseBody, uri);
    }

    public String parseToString() {
        StringBuilder responseBuilder = new StringBuilder();

        responseBuilder.append(String.format("HTTP/%.1f %d %s ", httpVersion, statusCode, status));
        responseBuilder.append("\r\n");

        if (contentType != null) {
            responseBuilder.append("Content-Type: ").append(contentType);
            if (charSet != null) {
                responseBuilder.append(";").append(charSet).append(" ");
            }
            responseBuilder.append("\r\n");
        }

        if (location != null) {
            responseBuilder.append("Location: ").append(location);
            responseBuilder.append("\r\n");
        }

        responseBuilder.append("Content-Length: ").append(contentLength).append(" ");
        responseBuilder.append("\r\n");

        responseBuilder.append("\r\n");

        if (responseBody != null) {
            responseBuilder.append(responseBody);
        }

        return responseBuilder.toString();
    }
}
