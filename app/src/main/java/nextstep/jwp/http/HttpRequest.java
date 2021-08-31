package nextstep.jwp.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    public static final String QUERY_STRING_DELIMITER = "&";
    public static final String QUERY_KEY_VALUE_DELIMITER = "=";
    public static final int KEY_INDEX = 0;
    public static final int VALUE_INDEX = 1;

    private final HttpRequestHeader header;
    private final HttpRequestBody body;

    private HttpRequest(final HttpRequestHeader header, final HttpRequestBody body) {
        this.header = header;
        this.body = body;
    }

    public static HttpRequest ofStaticFile(final String url) {
        return new HttpRequest(
                new HttpRequestHeader(List.of("GET " + url + " HTTP/1.1 ")),
                null
        );
    }

    public static HttpRequest parseRequest(final BufferedReader inputStreamReader) throws IOException {
        final List<String> requestHeaders = parseRequestHeaders(inputStreamReader);
        final HttpRequestHeader httpRequestHeader = new HttpRequestHeader(requestHeaders);

        final String requestBody = parseRequestBody(httpRequestHeader.getContentLength(), inputStreamReader);
        final HttpRequestBody httpRequestBody = new HttpRequestBody(requestBody);

        return new HttpRequest(httpRequestHeader, httpRequestBody);
    }

    private static List<String> parseRequestHeaders(final BufferedReader inputStreamReader) throws IOException {
        final List<String> requestHeaders = new ArrayList<>();
        String line;
        while (!"".equals(line = inputStreamReader.readLine())) {
            if (line == null) {
                break;
            }
            requestHeaders.add(line);
        }
        return requestHeaders;
    }

    private static String parseRequestBody(final int contentLength, final BufferedReader inputStreamReader) throws IOException {
        char[] buffer = new char[contentLength];
        inputStreamReader.read(buffer, 0, contentLength);
        return new String(buffer);
    }

    public boolean isGet() {
        return HttpMethod.isGet(getHttpMethod());
    }

    public String getHttpMethod() {
        return header.getHttpMethod().toUpperCase();
    }

    public String getPath() {
        return header.getPath();
    }

    public Map<String, String> getQueryParameters() {
        return header.getQueryParameters();
    }

    public String getProtocol() {
        return header.getProtocol();
    }

    public Map<String, String> getPayload() {
        return body.getPayload();
    }
}
