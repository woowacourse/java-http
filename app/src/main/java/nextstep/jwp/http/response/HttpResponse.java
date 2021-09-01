package nextstep.jwp.http.response;

import nextstep.jwp.http.ContentType;
import nextstep.jwp.http.HttpStatus;
import nextstep.jwp.http.authentication.HttpCookie;

import java.util.Objects;
import java.util.UUID;

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
                    "Set-Cookie: %s",
                    "");

    private final String protocol;
    private final HttpStatus httpStatus;
    private final HttpCookie httpCookie;
    private final ContentType contentType;
    private final Integer contentLength;
    private final String location;
    private final String responseBody;

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final HttpCookie httpCookie,
                        final String location) {
        this(protocol, httpStatus, httpCookie, null, null, location, null);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final HttpCookie httpCookie,
                        final ContentType contentType,
                        final Integer contentLength,
                        final String responseBody) {
        this(protocol, httpStatus, httpCookie, contentType, contentLength, null, responseBody);
    }

    public HttpResponse(final String protocol,
                        final HttpStatus httpStatus,
                        final HttpCookie httpCookie,
                        final ContentType contentType,
                        final Integer contentLength,
                        final String location,
                        final String responseBody) {
        this.protocol = protocol;
        this.httpStatus = httpStatus;
        this.httpCookie = httpCookie;
        this.contentType = contentType;
        this.contentLength = contentLength;
        this.location = location;
        this.responseBody = responseBody;
    }

    public String toResponseMessage() {
        if (Objects.nonNull(location)) {
            return String.format(REDIRECT_RESPONSE_FORMAT,
                    protocol,
                    httpStatus.getCode(),
                    httpStatus.getMessage(),
                    location
            );
        }

        String header = String.format(OK_RESPONSE_FORMAT,
                protocol,
                httpStatus.getCode(),
                httpStatus.getMessage(),
                contentType.getMimeType(),
                contentLength
        );

        if (httpCookie.doesNotHaveJSession()) {
            header = header + String.format(COOKIE_FORMAT, UUID.randomUUID());
        }

        return header + "\r\n" + responseBody;
    }
}
