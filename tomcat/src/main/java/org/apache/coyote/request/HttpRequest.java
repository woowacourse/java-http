package org.apache.coyote.request;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import org.apache.coyote.http11.HttpMethod;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;
    private static final int HEADER_INDEX_SIZE = 2;
    private static final Logger log = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader headers;
    private final String body;

    private HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestStartLine startLine = HttpRequestStartLine.from(bufferedReader.readLine());
        HttpRequestHeader httpHeaders = HttpRequestHeader.from(createHttpHeaderMap(bufferedReader));

        if (!startLine.getMethod().name().equals("GET")) {
            return new HttpRequest(startLine, httpHeaders, null);
        }
        return new HttpRequest(startLine, httpHeaders, createBody(bufferedReader, httpHeaders.getContentLength()));
    }

    private static Map<String, List<String>> createHttpHeaderMap(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_DELIMITER))
                .filter(parts -> parts.length == HEADER_INDEX_SIZE)
                .collect(groupingBy(parts -> parts[HEADER_KEY_INDEX].trim(),
                        mapping(parts -> parts[HEADER_VALUE_INDEX].trim(), toList())));
    }

    private static String createBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getEndpoint() {
        return startLine.getEndPoint();
    }

    public HttpRequestHeader getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "startLine=" + startLine +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
