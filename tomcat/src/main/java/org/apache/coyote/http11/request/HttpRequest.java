package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private static final String BLANK = "";
    private static final String HEADER_DELIMITER = ": ";
    private static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;
    public static final String CONTENT_LENGTH = "Content-Length";

    private final RequestLine requestLine;
    private final RequestHeaders headers;
    private final RequestBody body;

    private HttpRequest(
            final RequestLine requestLine,
            final RequestHeaders headers,
            final RequestBody body
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(final BufferedReader br) throws IOException {
        final String firstLine = br.readLine();

        final RequestLine requestLine = RequestLine.from(firstLine);
        final RequestHeaders headers = getRequestHeaders(br);
        final RequestBody requestBody = getRequestBody(br, headers);

        return new HttpRequest(requestLine, headers, requestBody);
    }

    private static RequestHeaders getRequestHeaders(final BufferedReader br) throws IOException {
        final RequestHeaders headers = new RequestHeaders();
        String line = br.readLine();
        while (!BLANK.equals(line)) {
            String[] header = line.split(HEADER_DELIMITER);
            headers.set(header[KEY_INDEX], header[VALUE_INDEX]);
        }
        return headers;
    }

    private static RequestBody getRequestBody(final BufferedReader br, final RequestHeaders headers) throws IOException {
        if (!headers.contains(CONTENT_LENGTH)) {
            return null;
        }
        final int contentLength = Integer.parseInt(headers.get(CONTENT_LENGTH));
        final char[] buffer = new char[contentLength];
        br.read(buffer, 0, contentLength);
        return RequestBody.from(new String(buffer));
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public boolean containsQuery() {
        return requestLine.containsQuery();
    }

    public boolean containsQuery(final String key) {
        return requestLine.containsQuery(key);
    }

    public String getQueryParameter(final String queryKey) {
        return requestLine.getQueryParameter(queryKey);
    }
}
