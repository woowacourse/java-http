package org.apache.coyote.http11;

import org.apache.coyote.HttpMethod;
import org.apache.coyote.HttpRequestStartLine;

public class Http11RequestStartLineParser {

    public static HttpRequestStartLine parse(String line) {
        String[] startLine = parseStartLine(line);
        HttpMethod httpMethod = parseHttpMethod(startLine);
        String requestURI = parseRequestURI(startLine);
        return new HttpRequestStartLine(httpMethod, requestURI);
    }

    private static String[] parseStartLine(String line) {
        final String[] startLine = line.split(" ");
        if (startLine.length != 3) {
            throw new IllegalArgumentException("HttpRequest의 startLine 형식이 잘못되었습니다.");
        }
        return startLine;
    }

    private static HttpMethod parseHttpMethod(String[] startLine) {
        String httpMethodName = startLine[0];
        return HttpMethod.findByName(httpMethodName);
    }

    private static String parseRequestURI(String[] startLine) {
        return startLine[1];
    }
}
