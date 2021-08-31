package nextstep.jwp.http;

public class HttpResponse {
    private static final String OK_RESPONSE_FORMAT =
            String.join("\r\n",
                    "%s %s %s ",
                    "Content-Type: %s;charset=utf-8 ",
                    "Content-Length: %d ",
                    ""
            );

    private static final String REDIRECT_RESPONSE_FORMAT =
            String.join("\r\n",
                    "%s %s %s ",
                    "Location: %s",
                    ""
            );

    private final String protocol;
    private final HttpStatus httpStatus;
    private final ContentType contentType;
    private final Integer contentLength;
    private final String location;
    private final String responseBody;

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final String location) {
        this(protocol, httpStatus, null, null, location, null);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final ContentType contentType,
                        final Integer contentLength,
                        final String responseBody) {
        this(protocol, httpStatus, contentType, contentLength, null, responseBody);
    }

    public HttpResponse(String protocol, HttpStatus httpStatus, ContentType contentType, Integer contentLength, String location, String responseBody) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.location = location;
        this.responseBody = responseBody;
    }

    public String toResponseMessage() {
        if (location == null) {
            String header = String.format(OK_RESPONSE_FORMAT,
                    protocol,
                    httpStatus.getCode(),
                    httpStatus.getMessage(),
                    contentType.getMimeType(),
                    contentLength
            );

            return header + "\r\n" + responseBody;
        }

        return String.format(REDIRECT_RESPONSE_FORMAT,
                protocol,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                location
        );
    }
}
