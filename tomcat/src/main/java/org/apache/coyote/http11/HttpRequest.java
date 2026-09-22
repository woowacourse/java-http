package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String requestTarget;
    private final Map<String, String> queryParameters;
    private final String body;

    public HttpRequest(String method, String requestTarget, Map<String, String> queryParameters, String body) {
        this.method = method;
        this.requestTarget = requestTarget;
        this.queryParameters = queryParameters;
        this.body = body;
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        final var requestLine = bufferedReader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            throw new IllegalArgumentException("Request line is blank");
        }
        final var requestLineParts = requestLine.trim().split(" ");
        if (requestLineParts.length != 3) {
            throw new IllegalArgumentException("Invalid request line");
        }
        final String method = requestLineParts[0];

        final var requestTargetParts = requestLineParts[1].split("\\?", 2);
        final String requestTarget = requestTargetParts[0];

        int contentLength = 0;
        String headerLine;
        while (!(headerLine = bufferedReader.readLine()).isBlank()) {
            if (headerLine.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(headerLine.split(":", 2)[1].trim());
            }
        }

        char[] bodyCharacters = new char[contentLength];
        int readLength = 0;
        while (readLength < contentLength) {
            int currentReadLength = bufferedReader.read(bodyCharacters, readLength, contentLength - readLength);
            if (currentReadLength == -1) {
                throw new IOException("Request body ended unexpectedly");
            }
            readLength += currentReadLength;
        }
        String body = new String(bodyCharacters);

        return new HttpRequest(method, requestTarget, initQueryParameters(requestTargetParts), body);
    }

    private static Map<String, String> initQueryParameters(String[] requestTargetParts) {
        final var queryParameters = new HashMap<String, String>();
        if (requestTargetParts.length < 2) {
            return queryParameters;
        }

        final var queryPairs = requestTargetParts[1].split("&");
        for (String queryPair : queryPairs) {
            var nameAndValue = queryPair.split("=", 2);
            queryParameters.put(nameAndValue[0], nameAndValue[1]);
        }
        return queryParameters;
    }

    public boolean isGetMethod() {
        return method.equals("GET");
    }

    public boolean isPostMethod() {
        return method.equals("POST");
    }

    public boolean isPath(String path) {
        return requestTarget.equals(path);
    }

    public String getMethod() {
        return method;
    }

    public String getRequestTarget() {
        return requestTarget;
    }

    public String getQueryParameter(String account) {
        return queryParameters.get(account);
    }

    public String getBody() {
        return body;
    }
}
