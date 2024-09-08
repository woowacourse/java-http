package org.apache.coyote.http11.request.domain;

import java.util.List;
import org.apache.coyote.http11.domain.protocolVersion.ProtocolVersion;

public class RequestLine {

    private static final int REQUEST_METHOD_INDEX = 0;
    private static final int REQUEST_PATH_INDEX = 1;
    private static final int REQUEST_PROTOCOL = 2;
    public static final String REQUEST_LINE_SEPARATOR = " ";

    private final RequestMethod requestMethod;
    private final RequestPath requestPath;
    private final ProtocolVersion protocolVersion;

    public RequestLine(String requestLine) {
        List<String> requestLines = splitToRequestLines(requestLine);

        this.requestMethod = RequestMethod.findMethod(requestLines.get(REQUEST_METHOD_INDEX).trim());
        this.requestPath = new RequestPath(requestLines.get(REQUEST_PATH_INDEX).trim());
        this.protocolVersion = new ProtocolVersion(requestLines.get(REQUEST_PROTOCOL).trim());
    }

    private List<String> splitToRequestLines(String requestLine) {
        List<String> requestLines = List.of(requestLine.split(REQUEST_LINE_SEPARATOR));

        if (requestLines.size() != 3) {
            throw new IllegalArgumentException("HttpLine의 형식이 옳바르지 않습니다.");
        }
        return requestLines;
    }

    public RequestPath getRequestPath() {
        return requestPath;
    }

    public String getPathValue() {
        return requestPath.getPath();
    }

    public String getRequestPathExtension() {
        return requestPath.findExtension();
    }

    public boolean isDefaultPath() {
        return requestPath.isDefaultPath();
    }

    public boolean isSameMethod(RequestMethod requestMethod) {
        return this.requestMethod.equals(requestMethod);
    }

    public boolean isStartsWith(String path) {
        return getPathValue().startsWith(path);
    }
}
