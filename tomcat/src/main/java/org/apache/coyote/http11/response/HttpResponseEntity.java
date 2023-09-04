package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.common.Constants.CRLF;
import static org.apache.coyote.http11.common.Constants.EMPTY;
import static org.apache.coyote.http11.common.Constants.SPACE;

import java.io.IOException;
import java.util.UUID;
import org.apache.coyote.http11.auth.HttpCookie;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.request.HttpRequestURI;

public class HttpResponseEntity {

    private final HttpRequestURI httpRequestURI;

    private HttpResponseEntity(final HttpRequestURI httpRequestURI) {
        this.httpRequestURI = httpRequestURI;
    }

    public static HttpResponseEntity from(final HttpRequestURI httpRequestURI) {
        return new HttpResponseEntity(httpRequestURI);
    }

    public String getResponse(final HttpCookie httpCookie) throws IOException {
        final var uri = httpRequestURI.getUri();
        final var uuid = UUID.randomUUID();
        final var responseHeader = HttpResponseHeader.from(httpRequestURI);
        final var responseBody = HttpResponseBody.from(httpRequestURI);

        final StringBuilder body = new StringBuilder().append(parseHttpStatusLine(responseHeader)).append(CRLF)
                .append(parseContentTypeLine(uri)).append(CRLF)
                .append(parseContentLengthLine(responseBody)).append(CRLF);

        if (httpRequestURI.isLoginSuccess() && httpCookie.noneJSessionId()) {
            body.append(parseCookieLine(uuid)).append(CRLF);
        }
        body.append(EMPTY).append(CRLF).append(responseBody.body());

        return body.toString();
    }

    private String parseCookieLine(final UUID uuid) {
        return String.format("Set-Cookie: JSESSIONID=%s", uuid);
    }

    private String parseHttpStatusLine(final HttpResponseHeader httpResponseHeader) {
        final HttpStatus httpStatus = httpResponseHeader.getHttpStatus();
        return String.join(
                SPACE,
                httpRequestURI.getHttpVersion(),
                String.valueOf(httpStatus.getCode()),
                httpStatus.name(),
                ""
        );
    }

    private String parseContentTypeLine(final String uri) {
        if (uri.endsWith(".css")) {
            return "Content-Type: text/css;charset=utf-8 ";
        }

        return "Content-Type: text/html;charset=utf-8 ";
    }

    private String parseContentLengthLine(final HttpResponseBody httpResponseBody) {
        return String.join(SPACE, "Content-Length:", String.valueOf(httpResponseBody.contentLength()), "");
    }

}
