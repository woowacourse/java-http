package com.techcourse.http.response;

import com.techcourse.http.common.ContentType;
import com.techcourse.http.common.HttpCookie;
import com.techcourse.http.common.HttpStatus;
import com.techcourse.http.common.HttpVersion;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HttpResponse {

    private final HttpVersion httpVersion;
    private final HttpStatus httpStatus;
    private final Location location;
    private final ContentType contentType;
    private final HttpCookie httpCookie;
    private final ResponseBody responseBody;

    public HttpResponse(final HttpVersion httpVersion,
                        final HttpStatus httpStatus,
                        final Location location,
                        final ContentType contentType,
                        final HttpCookie httpCookie,
                        final ResponseBody responseBody
    ) {
        this.httpVersion = httpVersion;
        this.httpStatus = httpStatus;
        this.location = location;
        this.contentType = contentType;
        this.httpCookie = httpCookie;
        this.responseBody = responseBody;
    }

    public static HttpResponse ok(
            final HttpVersion httpVersion, final ContentType contentType, final HttpCookie httpCookie,
            final ResponseBody responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(), contentType, httpCookie, responseBody);
    }

    public static HttpResponse noContent(
            final HttpVersion httpVersion, final ContentType contentType, final HttpCookie httpCookie,
            final ResponseBody responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.NO_CONTENT, Location.empty(), contentType, httpCookie,
                responseBody);
    }

    public static HttpResponse found(
            final HttpVersion httpVersion, final Location location, final ContentType contentType,
            final HttpCookie httpCookie
    ) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND, location, contentType, httpCookie,
                ResponseBody.empty());
    }

    public byte[] toBytes() {
        return toHttpResponseString().getBytes();
    }

    private String toHttpResponseString() {
        List<String> lines = new ArrayList<>();

        lines.add(httpVersion.toProtocolString() + " " + httpStatus.toStatusLine() + " ");
        lines.add("Content-Type: " + contentType.getMediaType() + ";charset=utf-8 ");
        lines.add("Content-Length: " + getContentLength() + " ");

        if (!location.isEmpty()) {
            lines.add(location.toHttpHeaderFormat());
        }
        if (!httpCookie.isEmpty()) {
            lines.add(httpCookie.toHttpHeaderFormat());
        }

        lines.add("");
        lines.add(responseBody.value());

        return String.join("\r\n", lines);
    }

    private int getContentLength() {
        return responseBody.value()
                .getBytes(StandardCharsets.UTF_8)
                .length;
    }
}
