package org.apache.coyote.http11.startline;

import java.nio.file.Path;
import org.apache.coyote.http11.header.RequestTarget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequestLine {

    private static final Logger log = LoggerFactory.getLogger(HttpRequestLine.class);

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final String httpVersion;

    public HttpRequestLine(String startLine) {
        String[] separatedStartLine = validateAndSplit(startLine);
        this.httpMethod = HttpMethod.valueOf(separatedStartLine[0]);
        this.requestTarget = new RequestTarget(separatedStartLine[1]);
        this.httpVersion = separatedStartLine[2];
    }

    private String[] validateAndSplit(String startLine) {
        String[] values = startLine.split(" ");
        if (values.length == 2) {
            return new String[]{values[0], "", values[1]};
        }
        if (values.length == 3) {
            return values;
        }
        RuntimeException exception = new IllegalArgumentException("Invalid start line: " + startLine);
        log.error(exception.getMessage(), exception);
        throw exception;
    }

    public boolean isTargetStatic() {
        return httpMethod.equals(HttpMethod.GET) && requestTarget.getPath() != null;
    }

    public boolean isTargetBlank() {
        return requestTarget.isBlank();
    }

    public boolean targetStartsWith(String startsWith) {
        return requestTarget.startsWith(startsWith);
    }

    public boolean targetEqualTo(String target) {
        return requestTarget.isEqualTo(target);
    }

    public String getTargetExtension() {
        return requestTarget.getTargetExtension();
    }

    public Path getTargetPath() {
        return requestTarget.getPath();
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }
}
