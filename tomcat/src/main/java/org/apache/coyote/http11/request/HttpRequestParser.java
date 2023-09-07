package org.apache.coyote.http11.request;

import org.apache.coyote.http11.HttpHeader;
import org.apache.coyote.http11.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequestParser {

    public HttpRequest parse(final BufferedReader reader) throws IOException {
        final String startLine = reader.readLine();
        validateEmpty(startLine);
        final String[] startLineTokens = startLine.split(" ");
        final HttpMethod httpMethod = HttpMethod.valueOf(startLineTokens[0]);
        final String pathWithQuery = startLineTokens[1];
        final String version = startLineTokens[2];
        final String[] pathWithQueryTokens = pathWithQuery.split("\\?");
        final String path = pathWithQueryTokens[0];
        final String query = findQuery(pathWithQuery, pathWithQueryTokens);

        final HttpHeader httpHeader = new HttpHeader(reader);
        final RequestBody requestBody = makeRequestBody(reader, httpHeader);

        return new HttpRequest(httpMethod, path, version, query, httpHeader, requestBody);
    }

    private void validateEmpty(final String startLine) throws IOException {
        if (startLine == null) {
            throw new IOException("Start Line is Empty");
        }
    }

    private String findQuery(final String pathWithQuery, final String[] pathWithQueryTokens) {
        if (pathWithQuery.contains("?")) {
            return pathWithQueryTokens[1];
        }
        return null;
    }

    private RequestBody makeRequestBody(final BufferedReader reader, final HttpHeader httpHeader) throws IOException {
        RequestBody requestBody = null;
        if (httpHeader.containContentLength()) {
            final int contentLength = httpHeader.getContentLength();
            final char[] body = new char[contentLength];
            reader.read(body, 0, contentLength);
            requestBody = new RequestBody(new String(body));
        }
        return requestBody;
    }
}
