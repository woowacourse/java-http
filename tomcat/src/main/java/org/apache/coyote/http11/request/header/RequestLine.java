package org.apache.coyote.http11.request.header;

import java.nio.file.Path;
import java.util.List;

public class RequestLine {

    private static final int REQUEST_LINE_INDEX = 0;

    private final Method method;
    private final Location location;

    public RequestLine(final Method method, final Location location) {
        this.method = method;
        this.location = location;
    }

    public static RequestLine from(final List<String> headers) {
        final String line = headers.get(REQUEST_LINE_INDEX);

        final Method method = Method.from(line);
        final Location location = Location.from(line);

        return new RequestLine(method, location);
    }

    public boolean isMethodEquals(final Method method) {
        return method.equals(this.method);
    }

    public String getContentType() {
        return location.contentType();
    }

    public Path getPath() {
        return location.getPath();
    }
}
