package org.apache.coyote.http11.httprequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private static final String HEADER_SEPARATOR = ": ";
    private static final String REQUEST_LINE_SEPARATOR = " ";
    private static final String QUERY_STRING_REGEX = "\\?";
    private static final String QUERY_STRING_SEPARATOR = "?";
    private static final String QUERY_PARAMETER_SEPARATOR = "&";
    private static final String QUERY_PARAMETER_KEY_VALUE_SEPARATOR = "=";

    private String requestLine;
    private HttpMethod httpMethod;
    private final Map<String, String> requestHeaders;
    private final BufferedReader reader;
    private String httpRequestBody;

    public HttpRequest(final BufferedReader reader) {
        this.requestHeaders = new HashMap<>();
        this.reader = reader;
    }

    // TODO : 파싱하는 책임을 별도 클래스로 분리
    public void parseHttpRequest() throws IOException {
        requestLine = reader.readLine();
        httpMethod = HttpMethod.from(requestLine.split(" ")[0]);
        String line;

        while (!(line = reader.readLine()).isEmpty()) {
            final String[] header = line.split(HEADER_SEPARATOR);
            if (header.length != 2) {
                break;
            }
            final String headerKey = header[0];
            final String headerValue = header[1];
            requestHeaders.put(headerKey, headerValue);
        }

        int contentLength = Integer.parseInt(requestHeaders.getOrDefault("Content-Length", "0"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        httpRequestBody = new String(buffer);
    }

    public String getRequestUri() {
        return requestLine.split(REQUEST_LINE_SEPARATOR)[1];
    }

    public String getRequestPath() {
        final String requestUri = getRequestUri();
        return requestUri.split(QUERY_STRING_REGEX)[0];
    }

    public boolean isQueryStringExists() {
        final String requestUri = getRequestUri();
        return requestUri.contains(QUERY_STRING_SEPARATOR);
    }

    public Map<String, String> getQueryParameters() {
        final String[] queryParameters = getQueryString().split(QUERY_PARAMETER_SEPARATOR);
        return Arrays.stream(queryParameters)
                .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1],
                        (oldValue, newValue) -> newValue
                ));
    }

    private String getQueryString() {
        final String requestUri = getRequestUri();
        return requestUri.split(QUERY_STRING_REGEX)[1];
    }

    // TODO : 현재 application/x-www-form-urlencoded만 처리
    public Map<String, String> getRequestBody() {
        final String[] parameters = httpRequestBody.split(QUERY_PARAMETER_SEPARATOR);
        return Arrays.stream(parameters)
                .map(param -> param.split(QUERY_PARAMETER_KEY_VALUE_SEPARATOR))
                .collect(Collectors.toMap(
                        param -> param[0],
                        param -> param[1],
                        (oldValue, newValue) -> newValue
                ));
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }
}
