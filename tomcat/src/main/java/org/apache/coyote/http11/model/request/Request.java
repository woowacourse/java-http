package org.apache.coyote.http11.model.request;

public class Request {

    private static final String LINE_SEPARATOR = "\r\n";
    private static final int START_LINE_INDEX = 0;

    private final RequestLine startLine;

    public Request(final String request) {
        String[] lines = request.split(LINE_SEPARATOR);
        this.startLine = new RequestLine(lines[START_LINE_INDEX]);
    }

    public String getMethod() {
        return startLine.getMethod();
    }

    public String getUrl() {
        return startLine.getUrl();
    }
}
