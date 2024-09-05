package org.apache.coyote.http11.request;

public class HttpRequestParser {

    public static String[] parseStartLine(String startLine) {
        String[] parsedStartLine = startLine.split(" ");
        if (parsedStartLine.length != 3) {
            throw new IllegalArgumentException("Invalid start line: " + startLine);
        }
        return parsedStartLine;
    }
}
