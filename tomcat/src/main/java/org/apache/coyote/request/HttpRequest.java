package org.apache.coyote.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.common.ContentType;
import org.apache.coyote.common.HttpMethod;
import org.apache.coyote.common.HttpVersion;

public class HttpRequest {

    private static final String ACCEPT_DELIMITER = ",";
    private static final String ACCEPT_QUALITY_DELIMITER = ";";

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeaders httpRequestHeaders;
    private final HttpRequestBody httpRequestBody;

    private HttpRequest(
            final HttpRequestLine httpRequestLine,
            final HttpRequestHeaders httpRequestHeaders,
            final HttpRequestBody httpRequestBody
    ) {
        this.httpRequestLine = httpRequestLine;
        this.httpRequestHeaders = httpRequestHeaders;
        this.httpRequestBody = httpRequestBody;
    }

    public static HttpRequest parse(final BufferedReader bufferedReader) throws IOException {
        final HttpRequestLine httpRequestLine = parseRequestLine(bufferedReader);
        final HttpRequestHeaders httpRequestHeaders = parseRequestHeaders(bufferedReader);
        final HttpRequestBody httpRequestBody = parseRequestBody(bufferedReader, httpRequestHeaders);
        return new HttpRequest(httpRequestLine, httpRequestHeaders, httpRequestBody);
    }

    private static HttpRequestLine parseRequestLine(final BufferedReader bufferedReader) throws IOException {
        final String line = bufferedReader.readLine();
        return HttpRequestLine.parse(line);
    }

    private static HttpRequestHeaders parseRequestHeaders(final BufferedReader bufferedReader) throws IOException {
        final List<String> lines = new ArrayList<>();
        String line;
        while ((line = bufferedReader.readLine()) != null && !"".equals(line)) {
            lines.add(line);
        }
        return HttpRequestHeaders.parse(lines);
    }

    private static HttpRequestBody parseRequestBody(
            final BufferedReader bufferedReader,
            final HttpRequestHeaders httpRequestHeaders
    ) throws IOException {
        final String contentLength = httpRequestHeaders.getHeader("Content-Length");
        if (contentLength == null) {
            return new HttpRequestBody();
        }
        final int length = Integer.parseInt(contentLength);
        final char[] buffer = new char[length];
        bufferedReader.read(buffer, 0, length);
        return new HttpRequestBody(new String(buffer));
    }

    public HttpMethod getHttpMethod() {
        return httpRequestLine.getHttpMethod();
    }

    public String getUriPath() {
        return httpRequestLine.getUriPath();
    }

    public HttpVersion getHttpVersion() {
        return httpRequestLine.getHttpVersion();
    }

    public List<Accept> getAccepts() {
        final String accepts = httpRequestHeaders.getHeader("Accept");

        if (accepts != null) {
            return Arrays.stream(accepts.split(ACCEPT_DELIMITER))
                    .map(accept -> accept.trim().split(ACCEPT_QUALITY_DELIMITER))
                    .map(Accept::from)
                    .sorted((o1, o2) -> Double.compare(o2.getQuality(), o1.getQuality()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public HttpCookie getCookie() {
        return httpRequestHeaders.getCookie();
    }

    public Map<String, String> getBody(final ContentType contentType) {
        return httpRequestBody.getBody(contentType);
    }
}
