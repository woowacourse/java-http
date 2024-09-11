package org.apache.coyote.request;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.coyote.http11.HttpMethod;
import org.apache.coyote.http11.HttpVersion;
import org.apache.coyote.http11.Cookie;

public class HttpRequest {

    private static final String HEADER_DELIMITER = ": ";
    private static final int HEADER_KEY_INDEX = 0;
    private static final int HEADER_VALUE_INDEX = 1;

    private final HttpRequestStartLine startLine;
    private final HttpRequestHeader headers;
    private final HttpRequestBody body;

    private HttpRequest(HttpRequestStartLine startLine, HttpRequestHeader headers, HttpRequestBody body) {
        this.startLine = startLine;
        this.headers = headers;
        this.body = body;
    }

    public static HttpRequest from(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        HttpRequestStartLine startLine = HttpRequestStartLine.from(bufferedReader.readLine());
        HttpRequestHeader httpHeaders = new HttpRequestHeader(readHeader(bufferedReader));
        HttpRequestBody httpRequestBody = new HttpRequestBody(readBody(bufferedReader, httpHeaders.getContentLength()));
        if (!startLine.getMethod().isGet()) {
            return new HttpRequest(startLine, httpHeaders, httpRequestBody);
        }
        return new HttpRequest(startLine, httpHeaders, null);
    }

    private static Map<String, List<String>> readHeader(BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .map(line -> line.split(HEADER_DELIMITER))
                .collect(groupingBy(header -> header[HEADER_KEY_INDEX].trim(),
                        mapping(header -> header[HEADER_VALUE_INDEX].trim(), toList())
                ));
    }

    private static String readBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        char[] buffer = new char[contentLength];
        int bytesRead = bufferedReader.read(buffer, 0, contentLength);
        String body = new String(buffer, 0, bytesRead);
        return URLDecoder.decode(body, StandardCharsets.UTF_8);
    }

    public HttpMethod getMethod() {
        return startLine.getMethod();
    }

    public String getTargetPath() {
        return startLine.getTargetPath();
    }

    public HttpVersion getVersion() {
        return startLine.getVersion();
    }

    public QueryParameters getTargetQueryParameters() {
        return startLine.getQueryParameters();
    }

    public HttpRequestHeader getHeaders() {
        return headers;
    }

    public Cookie getCookie() {
        return headers.getCookie();
    }

    public boolean hasCookie() {
        return headers.hasCookie();
    }

    public String getSessionId() {
        return headers.getCookie().getSessionId();
    }

    public boolean hasSession() {
        return headers.hasSession();
    }

    public HttpRequestBody getBody() {
        return body;
    }

    @Override
    public String toString() {
        if (body == null) {
            return String.join(
                    "\r\n",
                    startLine.toString(),
                    headers.toString(),
                    ""
            );
        }
        return String.join(
                "\r\n",
                startLine.toString(),
                headers.toString(),
                "",
                body.toString()
        );
    }
}
