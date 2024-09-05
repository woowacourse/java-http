package org.apache.coyote.http11;

public class Http11StartLineParser {

    private static final String CRLF = "\r\n";

    public String parseStartLine(String requestMessage) {
        String startLine = requestMessage.split(CRLF)[0];
        validateStartLine(startLine);
        return startLine;
    }

    private void validateStartLine(String startLine) {
        String[] split = startLine.split(" ");
        if (split.length != 3) {
            throw new IllegalStateException("올바른 HTTP 요청이 아닙니다.");
        }
    }
}
