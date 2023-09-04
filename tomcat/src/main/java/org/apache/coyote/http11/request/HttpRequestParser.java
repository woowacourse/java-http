package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {

    public Http11Request parse(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        if (startLine == null) {
            throw new IOException("Start Line is Empty");
        }
        final String[] startLineTokens = startLine.split(" ");
        final HttpMethod httpMethod = HttpMethod.valueOf(startLineTokens[0]);
        final String pathWithQuery = startLineTokens[1];
        final String[] pathWithQueryTokens = pathWithQuery.split("\\?");
        final String path = pathWithQueryTokens[0];
        final String query = findQuery(pathWithQuery, pathWithQueryTokens);

        final HttpHeader httpHeader = new HttpHeader(reader);

        RequestBody requestBody = null;
        if (httpHeader.containContentLength()) {
            final int contentLength = httpHeader.getContentLength();
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
