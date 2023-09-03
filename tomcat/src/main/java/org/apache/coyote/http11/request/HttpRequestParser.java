package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class HttpRequestParser {

    public Http11Request parse(final InputStream inputStream) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        final String startLine = reader.readLine();
        final String[] startLineTokens = startLine.split(" ");
        final HttpMethod httpMethod = HttpMethod.valueOf(startLineTokens[0]);
        final String pathWithQuery = startLineTokens[1];
        final String[] pathWithQueryTokens = pathWithQuery.split("\\?");
        final String path = pathWithQueryTokens[0];
        final String query = findQuery(pathWithQuery, pathWithQueryTokens);

        final HttpHeader httpHeader = new HttpHeader();
        String headerLine;
        while (!(headerLine = reader.readLine()).isEmpty()) {
            final String[] headerParts = headerLine.split(": ");
            httpHeader.put(headerParts[0], headerParts[1]);
        }

        RequestBody requestBody = null;
        if (httpHeader.containsKey("Content-Length")) {
            final int contentLength = Integer.parseInt(httpHeader.get("Content-Length"));
            final char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            requestBody = new RequestBody(new String(body));
        }

        return new Http11Request(httpMethod, path, query, httpHeader, requestBody);
    }

    private String findQuery(final String pathWithQuery, final String[] pathWithQueryTokens) {
        if (pathWithQuery.contains("?")) {
            return pathWithQueryTokens[1];
        }
        return null;
    }
}
