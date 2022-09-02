package org.apache.coyote.request;

import org.apache.coyote.exception.HttpException;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.HttpVersion;

public class StartLine {

    private static final String DELIMITER = " ";
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
        if (elements.length != 3) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        final var method = HttpMethod.valueOf(elements[METHOD_INDEX]);
        final var uri = elements[URI_INDEX];
        final var version = HttpVersion.valueOf(elements[VERSION_INDEX]);
        return new StartLine(method, uri, version);
    }
}
