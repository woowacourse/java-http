package org.apache.coyote.request;

import org.apache.coyote.exception.HttpException;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.HttpVersion;

public class StartLine {

    private static final String DELIMITER = " ";
    private static final int START_LINE_ELEMENT_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    private StartLine(HttpMethod method, String uri, HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static StartLine of(String startLine) {
        final var elements = startLine.split(DELIMITER);
        if (elements.length != START_LINE_ELEMENT_COUNT) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        final var method = toValidMethod(elements[METHOD_INDEX]);
        final var uri = toUri(elements[URI_INDEX]);
        final var version = toValidVersion(elements[VERSION_INDEX]);
        return new StartLine(method, uri, version);
    }

    private static HttpMethod toValidMethod(String method) {
        try {
            return HttpMethod.valueOf(method);
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }

    private static String toUri(String uri) {
        if (!uri.startsWith("/")) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        return uri;
    }

    private static HttpVersion toValidVersion(String version) {
        try {
            return HttpVersion.valueOf(version);
        } catch (IllegalArgumentException e) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }
}
