package nextstep.jwp.http.response;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpStatus;

import java.util.Objects;

public class HttpResponse {
    private static final String OK_RESPONSE_FORMAT =
            String.join("\r\n",
                    "%s %s %s ",
                    "Content-Type: %s;charset=utf-8 ",
                    "Content-Length: %d ",
                    "");

    private static final String REDIRECT_RESPONSE_FORMAT =
            String.join("\r\n",
                    "%s %s %s ",
                    "Location: %s",
                    "");

    private static final String COOKIE_FORMAT =
            String.join("\r\n",
                    "Set-Cookie: JSESSIONID=%s",
                    "");

    private final String protocol;
    private final HttpStatus httpStatus;
    private final String jSession;
    private final ContentType contentType;
    private final Integer contentLength;
    private final String location;
    private final String responseBody;

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final String location) {
        this(protocol, httpStatus, null, null, null, location, null);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final String jSession,
                        final String location) {
        this(protocol, httpStatus, jSession, null, null, location, null);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final ContentType contentType,
                        final Integer contentLength,
                        final String responseBody) {
        this(protocol, httpStatus, null, contentType, contentLength, null, responseBody);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final String jSession,
                        final ContentType contentType,
                        final Integer contentLength,
                        final String responseBody) {
        this(protocol, httpStatus, jSession, contentType, contentLength, null, responseBody);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final String jSession,
                        final ContentType contentType,
                        final Integer contentLength,
                        final String location,
                        final String responseBody) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.jSession = jSession;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.location = location;
        this.responseBody = responseBody;
    }

    public String toResponseMessage() {
        String header;
        if (Objects.isNull(location)) {
            header = String.format(OK_RESPONSE_FORMAT,
                    protocol,
                    httpStatus.getCode(),
                    httpStatus.getMessage(),
                    contentType.getMimeType(),
                    contentLength
            );

            return header + "\r\n" + responseBody;
        }

        header = String.format(REDIRECT_RESPONSE_FORMAT,
                protocol,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                location
        );

        if (Objects.nonNull(jSession)) {
            header += String.format(COOKIE_FORMAT, jSession);
        }

        return header;
    }
}
