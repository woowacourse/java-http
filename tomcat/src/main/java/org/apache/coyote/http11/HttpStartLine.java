package org.apache.coyote.http11;

import java.net.URL;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpStartLine {

    private static final Logger log = LoggerFactory.getLogger(HttpStartLine.class);

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;

    public HttpStartLine(String startLine) {
        String[] separatedStartLine = validateAndSplit(startLine);
        this.httpMethod = new HttpMethod(separatedStartLine[0]);
        this.requestTarget = new RequestTarget(separatedStartLine[1]);
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

    public Map<String, String> parseQueryString() {
        return requestTarget.parseQueryString();
    }

    public String getTargetExtension() {
        return requestTarget.getTargetExtension();
    }

    public boolean targetStartsWith(String path) {
        return requestTarget.startsWith(path);
    }

    public URL getTargetUrl() {
        return requestTarget.getUrl();
    }
}
