package org.apache.coyote.http.response;

import java.util.ArrayList;
import java.util.List;
import org.apache.coyote.http.ContentType;
import org.apache.coyote.http.HttpCookie;
import org.apache.coyote.http.HttpVersion;

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
            final HttpVersion httpVersion, final ContentType contentType, final ResponseBody responseBody
    ) {
        return new HttpResponse(httpVersion, HttpStatus.OK, Location.empty(), contentType, HttpCookie.empty(),
                responseBody);
    }

    public static HttpResponse noContent(
            final HttpVersion httpVersion, final ContentType contentType
    ) {
        return new HttpResponse(httpVersion, HttpStatus.NO_CONTENT, Location.empty(), contentType, HttpCookie.empty(),
                ResponseBody.empty());
    }

    public static HttpResponse found(
            final HttpVersion httpVersion, final Location location, final HttpCookie httpCookie
    ) {
        return new HttpResponse(httpVersion, HttpStatus.FOUND, location, null, httpCookie,
                ResponseBody.empty());
    }

    public byte[] toBytes() {
        return toHttpResponseString().getBytes();
    }

    private String toHttpResponseString() {
        List<String> lines = new ArrayList<>();

        lines.add(httpVersion.toProtocolString() + " " + httpStatus.toStatusLine() + " ");
        if (contentType != null) {
            lines.add(contentType.toHeaderLine() + " ");
        }
        if (!responseBody.isEmpty()) {
            lines.add(responseBody.toContentLengthHeaderLine() + " ");
        }
        if (!location.isEmpty()) {
            lines.add(location.toHttpHeaderFormat());
        }
        if (!httpCookie.isEmpty()) {
            lines.add(httpCookie.toHttpHeaderFormat());
        }

        lines.add("");
        if (!responseBody.isEmpty()) {
            lines.add(responseBody.value());
        }

        return String.join("\r\n", lines);
    }
}
