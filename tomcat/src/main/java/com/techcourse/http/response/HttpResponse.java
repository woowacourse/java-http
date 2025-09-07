package com.techcourse.http.response;

import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import java.nio.charset.StandardCharsets;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Location location;
    private final ContentType contentType;
    private final ResponseBody responseBody;

    public HttpResponse(final HttpVersion httpVersion,
                        final HttpStatus httpStatus,
                        final Location location,
                        final ContentType contentType,
                        final ResponseBody responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.location = location;
        this.contentType = contentType;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(
            final HttpVersion httpVersion, final ContentType contentType, final ResponseBody responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(), contentType, responseBody);
    }

    public static HttpResponse noContent(
            final HttpVersion httpVersion, final ContentType contentType, final ResponseBody responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.NO_CONTENT, Location.empty(), contentType, responseBody);
    }

    public static HttpResponse found(
            final HttpVersion httpVersion, final Location location, final ContentType contentType
    ) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND, location, contentType, ResponseBody.empty());
    }

    public byte[] toBytes() {
        return toHttpResponseString().getBytes();
    }

    private String toHttpResponseString() {
        return String.join("\r\n",
                httpVersion.toProtocolString() + " " + httpStatus.toStatusLine() + " ",
                "Content-Type: " + contentType.getMediaType() + ";charset=utf-8 ",
                "Content-Length: " + getContentLength() + " ",
                location.toHttpHeaderFormat(),
                "",
                responseBody.value());
    }

    private int getContentLength() {
        return responseBody.value()
                .getBytes(StandardCharsets.UTF_8)
                .length;
    }
}
