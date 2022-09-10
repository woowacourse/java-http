package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.common.Headers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final RequestLine requestLine;
    private final Headers requestHeaders;
    private final RequestBody requestBody;


    private HttpRequest(final RequestLine requestLine, final Headers requestHeaders, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = requestBody;
    }

    public static HttpRequest from(final BufferedReader bufferedReader) {
        try {
            final RequestLine requestLine = RequestLine.from(bufferedReader.readLine());
            final Headers headers = readHeaders(bufferedReader);
            final RequestBody requestBody = readBody(bufferedReader, headers);
            return new HttpRequest(requestLine, headers, requestBody);
        } catch (final IOException e) {
            log.error("invalid input", e);
            throw new IllegalArgumentException("올바른 HttpRequest 형식이 아닙니다.");
        }
    }

    private static Headers readHeaders(final BufferedReader bufferedReader) throws IOException {
        final Headers headers = new Headers();
        while (bufferedReader.ready()) {
            final String line = bufferedReader.readLine();
            Objects.requireNonNull(line);
            if ("".equals(line)) {
                break;
            }
            headers.add(line);
        }
        return headers;
    }

    private static RequestBody readBody(final BufferedReader bufferedReader, final Headers headers) {
        try {
            return createRequestBody(bufferedReader, headers);
        } catch (final IOException e) {
            log.error("invalid input", e);
            throw new IllegalArgumentException("올바른 Request Body 형식이 아닙니다.");
        }
    }

    private static RequestBody createRequestBody(final BufferedReader bufferedReader, final Headers headers)
            throws IOException {
        if (bufferedReader.ready()) {
            final int contentLength = Integer.parseInt((String) headers.findField("Content-Length"));
            final char[] contents = new char[contentLength];
            bufferedReader.read(contents, 0, contentLength);

            return new RequestBody(new String(contents));
        }
        return new RequestBody("");
    }

    public Map<String, String> parseApplicationFormData() {
        return requestBody.parseApplicationFormData();
    }

    public Optional<String> findCookie(final String name) {
        return requestHeaders.findCookie(name);
    }

    public boolean isGet() {
        return requestLine.isGet();
    }

    public boolean isPost() {
        return requestLine.isPost();
    }

    public String getUri() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Headers getRequestHeaders() {
        return requestHeaders;
    }
}
