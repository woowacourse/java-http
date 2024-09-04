package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.http.HttpHeaders;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public class Http11Request {

    private static final BiPredicate<String, String> HEADER_FILTER = (a, b) -> true;
    private static final String HEADER_KEY_VALUE_DELIMITER = ": ";
    private static final String HEADER_DETAIL_DELIMITER = ";";
    private static final String HEADER_FIELD_DELIMITER = ",";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final Http11RequestStartLine startLine;
    private final HttpHeaders headers;
    private final String body;

    public Http11Request(Http11RequestStartLine startLine, HttpHeaders headers, String body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static Http11Request from(InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        Http11RequestStartLine startLine = Http11RequestStartLine.from(bufferedReader.readLine());
        HttpHeaders httpHeaders = HttpHeaders.of(createHttpHeaderMap(bufferedReader), HEADER_FILTER);
        if (startLine.getMethod().hasBody()) {
            return new Http11Request(startLine, httpHeaders, createBody(bufferedReader, getContentLength(httpHeaders)));
        }
        return new Http11Request(startLine, httpHeaders, null);
    }

    private static Map<String, List<String>> createHttpHeaderMap(BufferedReader bufferedReader) throws IOException {
        Map<String, List<String>> httpHeaders = new HashMap<>();
        while (true) {
            String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                break;
            }
            String[] header = line.split(HEADER_KEY_VALUE_DELIMITER);
            String key = header[HEADER_KEY_INDEX];
            String value = header[HEADER_VALUE_INDEX];

            if (value.contains(HEADER_DETAIL_DELIMITER)) {
                int endIndex = value.indexOf(HEADER_DETAIL_DELIMITER);
                value = value.substring(0, endIndex);
            }

            List<String> values = Arrays.stream(value.split(HEADER_FIELD_DELIMITER)).map(String::trim).toList();
            httpHeaders.put(key, values);
        }
        return httpHeaders;
    }

    private static int getContentLength(HttpHeaders httpHeaders) {
        return httpHeaders.firstValue("Content-Length").map(Integer::parseInt).orElse(0);
    }

    private static String createBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    public boolean isStaticResourceRequest() {
        return startLine.getMethod() == HttpMethod.GET && startLine.getEndPoint().contains(".");
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getEndpoint() {
        return startLine.getEndPoint();
    }

    public Http11RequestTarget getRequestTarget() {
        return startLine.getRequestTarget();
    }

    public Http11RequestStartLine getStartLine() {
        return startLine;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public Optional<String> getHeader(String key) {
        return headers.firstValue(key);
    }

    @Override
    public String toString() {
        return "Http11Request{" +
               "startLine=" + startLine +
               ", headers=" + headers +
               ", body='" + body + '\'' +
               '}';
    }
}
