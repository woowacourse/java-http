package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestParser {

    public HttpRequest parse(InputStream inputStream) {
        final List<String> httpRequestMessages = getHttpRequestLines(inputStream);
        final String[] requestLine = httpRequestMessages.getFirst().split(" ");

        final var method = parseMethod(requestLine);
        final var path = parsePath(requestLine);
        final var queryParameter = parseQuaryParameter(requestLine);
        final var httpVersion = parseHttpVersion(requestLine);
        final var contentType = parseAccept(httpRequestMessages);

        return new HttpRequest(
                method,
                path,
                httpVersion,
                contentType,
                queryParameter
        );
    }

    private List<String> getHttpRequestLines(InputStream inputStream) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final List<String> httpRequestLines = bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .toList();
        if (httpRequestLines.isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청입니다.");
        }
        return httpRequestLines;
    }

    private Method parseMethod(String[] requestLine) {
        return Method.fromHeaderValue(requestLine[0].trim());
    }

    private String parsePath(String[] requestLine) {
        String requestTarget = requestLine[1].trim();
        if (requestTarget.contains("?")) {
            requestTarget = requestTarget.substring(0, requestTarget.indexOf("?"));
        }
        return requestTarget;
    }

    private Map<String, String> parseQuaryParameter(String[] requestLine) {
        final String requestTarget = requestLine[1].trim();
        if (requestTarget.contains("?")) {
            return parseQueryParameterPair(requestTarget);
        }
        return new HashMap<>();
    }

    private Map<String, String> parseQueryParameterPair(String requestTarget) {
        final Map<String, String> queryParameterPair = new HashMap<>();
        final String queryString = requestTarget.substring(requestTarget.indexOf("?") + 1);
        for (String pair : queryString.split("&")) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                queryParameterPair.put(keyValue[0], keyValue[1]);
            }
        }
        return queryParameterPair;
    }

    private HttpVersion parseHttpVersion(String[] requestLine) {
        return HttpVersion.fromHeaderValue(requestLine[2].trim());
    }

    private ContentType parseAccept(List<String> httpRequestMessages) {
        for (String line : httpRequestMessages) {
            if (line.startsWith("Accept:")) {
                String acceptValue = line.substring(("Accept: ").length()).trim();
                int semicolonIndex = acceptValue.indexOf(',');
                if (semicolonIndex != -1) {
                    acceptValue = acceptValue.substring(0, semicolonIndex).trim();
                }
                return ContentType.fromHeaderValue(acceptValue.trim());
            }
        }
        return ContentType.ALL;
    }
}
