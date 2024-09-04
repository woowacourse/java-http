package org.apache.coyote.http11;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpStartLine {

    private static final Logger log = LoggerFactory.getLogger(HttpStartLine.class);
    private static final String HTTP_METHOD = "HTTP Method";
    private static final String REQUEST_TARGET = "Request Target";
    private static final String HTTP_VERSION = "HTTP Version";

    private final Map<String, String> startLine;

    public HttpStartLine(String startLine) {
        String[] separatedStartLine = validateAndSplit(startLine);
        this.startLine = Map.of(
                HTTP_METHOD, separatedStartLine[0],
                REQUEST_TARGET, separatedStartLine[1],
                HTTP_VERSION, separatedStartLine[2]
        );
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

    public String getRequestTarget() {
        return "static" + startLine.get(REQUEST_TARGET);
    }

    public String getTargetExtension() {
        String requestTarget = startLine.get(REQUEST_TARGET);
        return requestTarget.substring(requestTarget.lastIndexOf(".") + 1);
    }
}
