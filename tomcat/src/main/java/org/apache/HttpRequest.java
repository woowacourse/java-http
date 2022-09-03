package org.apache;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequest {

    private String httpMethod;
    private String uri;
    private String httpVersion;
    private final Map<String, String> headerMap = new HashMap<>();
    private String body;

    public HttpRequest(final String httpMethod, final String uri, final String httpVersion, final String body) {
        this.httpMethod = httpMethod;
        this.uri = uri;
        this.httpVersion = httpVersion;
        this.body = body;
    }

    public HttpRequest(final InputStream inputStream) throws IOException {
        List<String> requestLines = createRequestLines(inputStream);
        int boundaryLineIndex = requestLines.indexOf("");

        initStartLine(requestLines);

        for (int i = 1; i < boundaryLineIndex; i++) {
            String[] requestLine = requestLines.get(i).split(":");
            headerMap.put(requestLine[0].trim(), requestLine[1].trim());
        }

        this.body = parseRequestBodyLines(requestLines, boundaryLineIndex + 1);
    }

    private List<String> createRequestLines(final InputStream inputStream) throws IOException {
        List<String> requestLines = new ArrayList<>();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while (bufferedReader.ready()) {
            requestLines.add(bufferedReader.readLine());
        }
        return requestLines;
    }

    private void initStartLine(final List<String> requestLines) {
        String[] startLine = requestLines.get(0).split(" ");
        this.httpMethod = startLine[0];
        this.uri = startLine[1];
        this.httpVersion = startLine[2];
    }

    private String parseRequestBodyLines(final List<String> requestLines, final int bodyLineIndex) {
        StringBuilder requestBody = new StringBuilder();
        for (int i = bodyLineIndex; i < requestLines.size(); i++) {
            requestBody.append(requestLines.get(i)).append("\n");
        }
        return requestBody.toString();
    }

    public static HttpRequest of(final String uri) {
        return new HttpRequest("GET", uri, "HTTP/1.1", "");
    }

    public boolean isStaticResourceRequest() {
        return this.uri.contains(".");
    }

    public String getStaticResourceType() {
        if (!isStaticResourceRequest()) {
            throw new IllegalStateException("[ERROR] 정적 파일과 관련된 요청이 아닙니다.");
        }
        return this.uri.split("\\.")[1];
    }

    public boolean isQueryString() {
        return this.uri.contains("?");
    }

    public Map<String, String> parseQueryString() {
        if (!isQueryString()) {
            throw new IllegalStateException("[ERROR] 쿼리 스트링이 포함되지 않은 요청입니다.");
        }
        String[] queryString = this.uri.split("\\?")[1].split("&");
        Map<String, String> queryStringMap = Arrays.stream(queryString)
                .collect(Collectors.toMap(i -> i.split("=")[0], i -> i.split("=")[1]));
        return queryStringMap;
    }

    public String getHeaderValue(final String key) {
        return headerMap.get(key);
    }

    public String getStartLine() {
        return httpMethod + " " + uri + " " + httpVersion;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        if (isQueryString()) {
            return uri.split("\\?")[0];
        }
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getBody() {
        return body;
    }
}
