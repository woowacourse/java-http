package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.Query;

public class StartLine {

    private static final String START_LINE_DELIMITER = " ";

    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion httpVersion;

    private StartLine(HttpMethod httpMethod, RequestTarget requestTarget, HttpVersion httpVersion) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.httpVersion = httpVersion;
    }

    public static StartLine create(String startLine) {
        String[] splitStartLine = startLine.split(START_LINE_DELIMITER);

        try {
            HttpMethod httpMethod = HttpMethod.valueOf(splitStartLine[0]);
            RequestTarget requestTarget = RequestTarget.create(splitStartLine[1]);
            HttpVersion httpVersion = HttpVersion.findByValue(splitStartLine[2]);
            return new StartLine(httpMethod, requestTarget, httpVersion);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid startLine format");
        }
    }

    public String path() {
        return this.requestTarget.path();
    }

    public Query getQueries() {
        return requestTarget.getQueries();
    }

    public HttpMethod httpMethod() {
        return this.httpMethod;
    }

    public HttpVersion httpVersion() {
        return this.httpVersion;
    }
}
