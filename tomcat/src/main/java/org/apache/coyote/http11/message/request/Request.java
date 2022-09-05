package org.apache.coyote.http11.message.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.coyote.http11.message.header.Header;
import org.apache.coyote.http11.message.request.body.RequestBody;
import org.apache.coyote.http11.message.request.header.Headers;
import org.apache.coyote.http11.message.request.requestline.RequestLine;

public class Request {

    private final RequestLine requestLine;
    private final Headers headers;
    private final RequestBody requestBody;

    public Request(final RequestLine requestLine, final Headers headers, final RequestBody requestBody) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.requestBody = requestBody;
    }

    public static Request from(final BufferedReader requestReader) throws IOException {
        final String rawRequestLine = Objects.requireNonNull(requestReader.readLine());
        final RequestLine requestLine = RequestLine.from(rawRequestLine);

        final List<String> rawHeaders = readHeaders(requestReader);
        final Headers headers = Headers.from(rawHeaders);

        final Optional<String> contentLength = headers.get(Header.CONTENT_LENGTH);
        if (contentLength.isPresent()) {
            final String body = readBody(requestReader, Integer.parseInt(contentLength.get()));
            return new Request(requestLine, headers, RequestBody.from(body));
        }
        return new Request(requestLine, headers, RequestBody.ofEmpty());
    }

    private static List<String> readHeaders(final BufferedReader requestReader) throws IOException {
        final List<String> headers = new ArrayList<>();
        String line = requestReader.readLine();
        while (!"".equals(line)) {
            headers.add(line);
            line = requestReader.readLine();
        }
        return headers;
    }

    private static String readBody(final BufferedReader requestReader, final int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        requestReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isPath(final String path) {
        return requestLine.isPath(path);
    }

    public boolean isForResource() {
        return requestLine.isForResource();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public QueryParams getUriQueryParams() {
        return requestLine.getQueryParams();
    }
}
