package org.apache.coyote.http11.response;

import static org.apache.coyote.http11.response.HttpStatusCode.FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.NOT_FOUND;
import static org.apache.coyote.http11.response.HttpStatusCode.OK;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.coyote.http11.converter.HeaderConverter;

public class HttpResponse {

    private static final List<String> EMPTY_HEADERS = Collections.emptyList();
    private static final String EMPTY_BODY = "";

    private final HttpStatusCode statusCode;
    private final List<String> headers;
    private final String body;

    public HttpResponse(final HttpStatusCode statusCode, final List<String> headers,
        final String body) {
        this.statusCode = statusCode;
        this.headers = new ArrayList<>(headers);
        this.body = body;
    }

    public static HttpResponse ok(final ContentType contentType, final String body) {
        return simpleBody(OK, contentType, body);
    }

    public static HttpResponse simpleBody(
        final HttpStatusCode statusCode,
        final ContentType contentType,
        final String body
    ) {
        final List<String> headers = List.of(
            HeaderConverter.toContentType(contentType),
            HeaderConverter.toContentLength(body)
        );

        return new HttpResponse(statusCode, headers, body);
    }

    public static HttpResponse notFound() {
        return new HttpResponse(NOT_FOUND, EMPTY_HEADERS, EMPTY_BODY);
    }

    public static HttpResponse redirect(final String locationUrl) {
        return new HttpResponse(
            FOUND,
            Collections.singletonList(HeaderConverter.toLocation(locationUrl)),
            EMPTY_BODY
        );
    }

    public void addCookie(final HttpCookie cookie) {
        headers.add(HeaderConverter.toSetCookie(cookie.getValues()));
    }

    public String convertToMessage() {
        final StringBuilder message = new StringBuilder();

        message.append(getStatusLine()).append(System.lineSeparator());
        headers.forEach(
            header -> message.append(header)
                .append(" ")
                .append(System.lineSeparator())
        );
        message.append(System.lineSeparator());
        message.append(body);

        return message.toString();
    }

    private String getStatusLine() {
        final String statusLineFormat = "HTTP/1.1 %d %s ";

        return String.format(statusLineFormat, statusCode.getCode(), statusCode.getMessage());
    }
}

