package org.apache.coyote.http11;

import java.util.List;

public class Http11Request {

    private static final String HTTP11_VERSION = "HTTP/1.1";

    private final List<String> lines;

    public Http11Request(List<String> lines) {
        this.lines = lines;
        validate();
    }

    private void validate() {
        String httpVersion = parseStartLine()[2];
        if (!HTTP11_VERSION.equals(httpVersion)) {
            throw new IllegalArgumentException("해당 Http version 은 지원하지 않습니다: " + httpVersion);
        }
    }

    public Http11Method getHttpMethod() {
        String httpMethodName = parseStartLine()[0];
        return Http11Method.findByName(httpMethodName);
    }

    public String getRequestURI() {
        return parseStartLine()[1];
    }

    private String[] parseStartLine() {
        final String[] startLine = lines.get(0).split(" ");
        if (startLine.length != 3) {
            throw new IllegalArgumentException("HttpRequest의 startLine 형식이 잘못되었습니다.");
        }
        return startLine;
    }
}
