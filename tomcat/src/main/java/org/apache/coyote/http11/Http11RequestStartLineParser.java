package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Http11RequestStartLineParser {

    public static Http11RequestStartLine parse(List<String> lines) {
        String[] startLine = parseStartLine(lines);
        String httpVersion = parseHttpVersion(startLine);
        Http11Method httpMethod = parseHttpMethod(startLine);
        String requestURI = parseRequestURI(startLine);
        Map<String, String> queryParameters = parseQueryParameters(requestURI);
        return new Http11RequestStartLine(httpVersion, httpMethod, requestURI, queryParameters);
    }

    private static String[] parseStartLine(List<String> lines) {
        final String[] startLine = lines.get(0).split(" ");
        if (startLine.length != 3) {
            throw new IllegalArgumentException("HttpRequest의 startLine 형식이 잘못되었습니다.");
        }
        return startLine;
    }

    private static String parseHttpVersion(String[] startLine) {
        return startLine[2];
    }

    private static Http11Method parseHttpMethod(String[] startLine) {
        String httpMethodName = startLine[0];
        return Http11Method.findByName(httpMethodName);
    }

    private static String parseRequestURI(String[] startLine) {
        return startLine[1];
    }

    private static Map<String, String> parseQueryParameters(String requestURI) {
        Map<String, String> queryParameters = new HashMap<>();
        if (requestURI.contains("?")) {
            int index = requestURI.indexOf("?");
            String queryString = requestURI.substring(index + 1);
            for (String eachQueryString : queryString.split("&")) {
                String[] parsedEachQueryString = eachQueryString.split("=");
                queryParameters.put(parsedEachQueryString[0], parsedEachQueryString[1]);
            }
        }
        return queryParameters;
    }
}
