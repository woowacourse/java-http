package org.apache;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HttpRequestMessage {

    private String httpMethod;
    private String uri;
    private String httpVersion;
    private final Map<String, String> headerMap = new HashMap<>();
    private String body;

    public HttpRequestMessage(InputStream inputStream) {
        List<String> requestLines = new BufferedReader(new InputStreamReader(inputStream))
                .lines()
                .collect(Collectors.toList());

        int boarderLine = requestLines.indexOf("");

        initStartLine(requestLines);

        for (int i = 1; i < boarderLine; i++) {
            String[] requestLine = requestLines.get(i).split(":");
            headerMap.put(requestLine[0].trim(), requestLine[1].trim());
        }

        this.body = parseRequestBodyLines(requestLines, boarderLine + 1);
    }

    private void initStartLine(final List<String> requestLines) {
//        System.out.println("###");
//        System.out.println(requestLines.get(0));
        String[] startLine = requestLines.get(0).split(" ");
        this.httpMethod = startLine[0];
        this.uri = startLine[1];
        this.httpVersion = startLine[2];
    }

    private String parseRequestBodyLines(List<String> requestLines, int bodyLineIndex) {
        StringBuilder requestBody = new StringBuilder();
        for (int i = bodyLineIndex; i < requestLines.size(); i++) {
            requestBody.append(requestLines.get(i) + "\n");
        }
        return requestBody.toString();
    }

    public boolean isStaticResourceRequest() {
        return getUri().contains(".");
    }

    public String getStaticResourceType() {
        if (!isStaticResourceRequest()) {
            throw new IllegalStateException("[ERROR] 정적 파일과 관련된 요청이 아닙니다.");
        }
        return getUri().split("\\.")[1];
    }

    public String getHeaderValue(String key) {
        return headerMap.get(key);
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getUri() {
        return uri;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public String getBody() {
        return body;
    }
}
