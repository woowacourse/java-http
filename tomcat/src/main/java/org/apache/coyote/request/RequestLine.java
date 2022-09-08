package org.apache.coyote.request;

import java.util.List;
import org.apache.coyote.request.exception.InvalidRequestLineFormatException;

public class RequestLine {

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final int PROTOCOL_VERSION_INDEX = 2;
    private static final int REQUEST_LINE_FORMAT_SIZE = 3;

    private final HttpMethod method;
    private final String path;
    private final String protocolVersion;

    private RequestLine(final HttpMethod method, final String path, final String protocolVersion) {
        this.method = method;
        this.path = path;
        this.protocolVersion = protocolVersion;
    }

    public static RequestLine parse(final String value) {
        final List<String> requestLine = List.of(value.split(" "));
        verifyRequestLineFormat(requestLine);

        return new RequestLine(
                HttpMethod.valueOf(requestLine.get(METHOD_INDEX)),
                requestLine.get(PATH_INDEX),
                requestLine.get(PROTOCOL_VERSION_INDEX)
        );
    }

    private static void verifyRequestLineFormat(final List<String> requestLine) {
        if (requestLine.size() != REQUEST_LINE_FORMAT_SIZE) {
            throw new InvalidRequestLineFormatException();
        }
    }

    public boolean isSameHttpMethod(final HttpMethod httpMethod) {
        return this.method.equals(httpMethod);
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocolVersion() {
        return protocolVersion;
    }
}
