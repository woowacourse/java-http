package org.apache.coyote.servlet.request;

import java.util.HashMap;
import org.apache.coyote.support.HttpException;
import org.apache.coyote.support.HttpMethod;
import org.apache.coyote.support.HttpStatus;
import org.apache.coyote.support.HttpVersion;

public class StartLine {

    private static final String DELIMITER = " ";
    private static final int START_LINE_ELEMENT_COUNT = 3;
    private static final int METHOD_INDEX = 0;
    private static final int URI_INDEX = 1;
    private static final int VERSION_INDEX = 2;
    private static final String QUERY_STRING_BEGIN_SIGN = "?";

    private final HttpMethod method;
    private final String uri;
    private final HttpVersion version;

    private StartLine(HttpMethod method, String uri, HttpVersion version) {
        this.method = method;
        this.uri = uri;
        this.version = version;
    }

    public static StartLine of(String startLine) {
        validate(startLine);
        final var elements = extract(startLine);
        final var method = HttpMethod.of(elements[METHOD_INDEX]);
        final var uri = toUri(elements[URI_INDEX]);
        final var version = HttpVersion.of(elements[VERSION_INDEX]);
        return new StartLine(method, uri, version);
    }

    private static void validate(String startLine) {
        if (startLine == null || startLine.length() == 0) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
    }

    private static String[] extract(String startLine) {
        final var elements = startLine.split(DELIMITER);
        if (elements.length != START_LINE_ELEMENT_COUNT) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        return elements;
    }

    private static String toUri(String uri) {
        if (!uri.startsWith("/")) {
            throw new HttpException(HttpStatus.BAD_REQUEST);
        }
        return uri;
    }

    public boolean hasMethodOf(HttpMethod method) {
        return this.method.equals(method);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getUri() {
        if (!uri.contains(QUERY_STRING_BEGIN_SIGN)) {
            return uri;
        }
        final var delimiterIndex = uri.indexOf(QUERY_STRING_BEGIN_SIGN);
        return uri.substring(0, delimiterIndex);
    }

    public Parameters getParameters() {
        if (!uri.contains(QUERY_STRING_BEGIN_SIGN)) {
            return new Parameters(new HashMap<>());
        }
        final var delimiterIndex = uri.indexOf(QUERY_STRING_BEGIN_SIGN);
        final var queryString = uri.substring(delimiterIndex + 1);
        return Parameters.of(queryString);
    }
}
