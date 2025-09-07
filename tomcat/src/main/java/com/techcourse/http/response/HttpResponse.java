package com.techcourse.http.response;

import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private static final String EMPTY_RESPONSE_BODY = "";

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Location location;
    private final ContentType contentType;
    private final String responseBody;

    public HttpResponse(final HttpVersion httpVersion,
                        final HttpStatus httpStatus,
                        final Location location,
                        final ContentType contentType,
                        final String responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.location = location;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(
            final HttpVersion httpVersion, final ContentType contentType, final String responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(), contentType, responseBody);
    }

    public static HttpResponse noContent(
            final HttpVersion httpVersion, final ContentType contentType, final String responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.NO_CONTENT, Location.empty(), contentType, responseBody);
    }

    public static HttpResponse found(
            final HttpVersion httpVersion, final Location location, final ContentType contentType
    ) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND, location, contentType, EMPTY_RESPONSE_BODY);
    }

    public byte[] toBytes() {
        return toHttpResponseString().getBytes();
    }

    private String toHttpResponseString() {
        return String.join("\r\n",
                httpVersion.toProtocolString() + " " + httpStatus.toStatusLine() + " ",
                "Content-Type: " + contentType.getMediaType() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                location.toHttpHeaderFormat(),
                "",
                responseBody);
    }
}
