package org.apache.coyote.http11;

public class Http11RequestStartLineParser {

    public static Http11RequestStartLine parse(String line) {
        String[] startLine = parseStartLine(line);
        Http11Method httpMethod = parseHttpMethod(startLine);
        String requestURI = parseRequestURI(startLine);
        return new Http11RequestStartLine(httpMethod, requestURI);
    }

    private static String[] parseStartLine(String line) {
        final String[] startLine = line.split(" ");
        if (startLine.length != 3) {
            throw new IllegalArgumentException("HttpRequest의 startLine 형식이 잘못되었습니다.");
        }
        return startLine;
    }

    private static Http11Method parseHttpMethod(String[] startLine) {
        String httpMethodName = startLine[0];
        return Http11Method.findByName(httpMethodName);
    }

    private static String parseRequestURI(String[] startLine) {
        return startLine[1];
    }
}
